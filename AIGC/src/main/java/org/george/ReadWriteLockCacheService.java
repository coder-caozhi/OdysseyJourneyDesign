package org.george;

import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 使用 Redisson 读写锁解决缓存击穿 + 缓存一致性问题
 *
 * 核心机制：
 *   - 读操作加共享锁（读锁）：读读不互斥，读写互斥
 *   - 写操作加排他锁（写锁）：读写互斥，写写互斥
 *   - 读和写必须使用同一把 RReadWriteLock
 *
 * 效果：
 *   - 多个线程可以同时读缓存（并发读）
 *   - 写数据时，所有读线程会被阻塞，避免脏数据
 *   - 写数据时，其他写线程也会被阻塞，保证写安全
 */
@Service
public class ReadWriteLockCacheService {

    private final StringRedisTemplate redisTemplate;
    private final RedissonClient redissonClient;
    private final DatabaseService databaseService;

    // ==================== 常量配置 ====================

    private static final String CACHE_PREFIX = "cache:";
    private static final long CACHE_TTL = 300L; // 缓存过期时间（秒）

    public ReadWriteLockCacheService(StringRedisTemplate redisTemplate,
                                     RedissonClient redissonClient,
                                     DatabaseService databaseService) {
        this.redisTemplate = redisTemplate;
        this.redissonClient = redissonClient;
        this.databaseService = databaseService;
    }

    // ==================== 核心：获取读写锁 ====================

    /**
     * 根据业务 key 获取对应的读写锁
     *
     * 关键点：同一个业务 key 的读方法和写方法必须调用这个方法获取同一把锁
     * 例如：cache:goods:1001 对应的锁 key 为 rwlock:goods:1001
     *
     * @param bizId 业务 ID
     * @return Redisson 读写锁对象
     */
    private RReadWriteLock getReadWriteLock(String bizId) {
        return redissonClient.getReadWriteLock("rwlock:" + bizId);
    }

    // ==================== 读操作（共享锁） ====================

    /**
     * 读取数据 —— 使用共享锁（读锁）
     *
     * 执行流程：
     *   1. 获取读锁（共享锁）
     *   2. 查 Redis 缓存
     *   3. 缓存命中 → 直接返回
     *   4. 缓存未命中 → 查 DB → 写缓存 → 返回
     *   5. 释放读锁
     *
     * 互斥关系：
     *   ✓ 读-读：不互斥（多个线程可以同时持有读锁，同时读）
     *   ✗ 读-写：互斥（有写锁时读锁等待，有读锁时写锁等待）
     *
     * @param bizId 业务 ID
     * @return 查询结果
     */
    public String getData(String bizId) {

        String cacheKey = CACHE_PREFIX + bizId;

        // 获取读锁（共享锁）
        RLock readLock = getReadWriteLock(bizId).readLock();

        try {
            // 加读锁 —— 会阻塞等待：如果当前有写锁持有者，读锁会等待
            readLock.lock();
            System.out.println("[线程 " + Thread.currentThread().getName()
                + "] 已获取读锁，开始读取 bizId=" + bizId);

            // ── 第一步：查缓存 ──
            String cachedValue = redisTemplate.opsForValue().get(cacheKey);
            if (cachedValue != null) {
                System.out.println("[线程 " + Thread.currentThread().getName()
                    + "] 缓存命中，直接返回");
                return cachedValue;
            }

            // ── 第二步：缓存未命中，查 DB 并回填缓存 ──
            System.out.println("[线程 " + Thread.currentThread().getName()
                + "] 缓存未命中，查询 DB");
            String dbValue = databaseService.queryById(bizId);

            // 写入缓存
            redisTemplate.opsForValue().set(cacheKey, dbValue, CACHE_TTL, TimeUnit.SECONDS);
            System.out.println("[线程 " + Thread.currentThread().getName()
                + "] 已将 DB 数据写入缓存");

            return dbValue;

        } finally {
            // 释放读锁
            readLock.unlock();
            System.out.println("[线程 " + Thread.currentThread().getName()
                + "] 已释放读锁");
        }
    }

    // ==================== 写操作（排他锁） ====================

    /**
     * 更新数据 —— 使用排他锁（写锁）
     *
     * 执行流程：
     *   1. 获取写锁（排他锁）—— 会等待所有读锁和写锁释放
     *   2. 更新数据库
     *   3. 删除旧缓存（或直接更新缓存）
     *   4. 释放写锁
     *
     * 互斥关系：
     *   ✗ 写-读：互斥（写锁持有期间，所有读锁请求阻塞）
     *   ✗ 写-写：互斥（同时只有一个线程能持有写锁）
     *
     * @param bizId  业务 ID
     * @param newVal 新值
     */
    public void updateData(String bizId, String newVal) {

        String cacheKey = CACHE_PREFIX + bizId;

        // 获取写锁（排他锁）
        RLock writeLock = getReadWriteLock(bizId).writeLock();

        try {
            // 加写锁 —— 会阻塞等待：直到所有读锁和写锁都释放
            writeLock.lock();
            System.out.println("[线程 " + Thread.currentThread().getName()
                + "] 已获取写锁，开始更新 bizId=" + bizId);

            // ── 第一步：更新数据库 ──
            databaseService.updateById(bizId, newVal);
            System.out.println("[线程 " + Thread.currentThread().getName()
                + "] DB 更新完成");

            // ── 第二步：删除旧缓存（Cache Aside Pattern）──
            redisTemplate.delete(cacheKey);
            System.out.println("[线程 " + Thread.currentThread().getName()
                + "] 已删除旧缓存，下次读取会自动回填");

            // ── 或者选择直接更新缓存 ──
            // redisTemplate.opsForValue().set(cacheKey, newVal, CACHE_TTL, TimeUnit.SECONDS);

        } finally {
            // 释放写锁
            writeLock.unlock();
            System.out.println("[线程 " + Thread.currentThread().getName()
                + "] 已释放写锁");
        }
    }

    // ==================== 模拟 DB 服务 ====================

    @Service
    public static class DatabaseService {

        public String queryById(String bizId) {
            try {
                Thread.sleep(200); // 模拟查询耗时
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return "{\"id\":\"" + bizId + "\",\"name\":\"商品数据\",\"price\":99.9}";
        }

        public void updateById(String bizId, String newVal) {
            try {
                Thread.sleep(100); // 模拟更新耗时
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println("  [DB] 已更新 bizId=" + bizId + " → " + newVal);
        }
    }
}
