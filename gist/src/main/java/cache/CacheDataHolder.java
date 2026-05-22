package cache;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 缓存数据持有者 —— 内存中的"数据库".
 *
 * <p>线程安全设计：
 * <ul>
 *   <li>使用 {@link ConcurrentHashMap} 保证多线程读写安全</li>
 *   <li>每次更新全量替换 value（不可变语义），避免并发遍历时读到中间态</li>
 * </ul>
 *
 * <p>存储结构：{@code Map<cacheKey, CacheData<?>>}
 */
@Slf4j
public final class CacheDataHolder {

    private CacheDataHolder() {
    }

    // ====================== 内存存储 ======================

    /** 按 cacheKey 分桶存储 */
    private static final ConcurrentHashMap<String, CacheData<?>> STORE = new ConcurrentHashMap<>();

    // ====================== 写操作 ======================

    /**
     * 全量替换指定 cacheKey 的数据.
     *
     * @param cacheKey 缓存标识（见 {@link CacheConstant}）
     * @param data     新数据
     */
    public static <T> void put(String cacheKey, CacheData<T> data) {
        if (cacheKey == null || data == null) {
            log.warn("CacheDataHolder.put() called with null key or data");
            return;
        }
        STORE.put(cacheKey, data);
        log.debug("CacheDataHolder [{}] updated, {} records, latestTime={}",
                cacheKey, data.getDataList().size(), data.getLatestUpdateTime());
    }

    // ====================== 读操作 ======================

    /**
     * 读取缓存数据（不指定类型，调用方自行转换）.
     */
    public static CacheData<?> get(String cacheKey) {
        return STORE.get(cacheKey);
    }

    /**
     * 读取缓存数据并做类型转换.
     */
    @SuppressWarnings("unchecked")
    public static <T> CacheData<T> getTyped(String cacheKey) {
        return (CacheData<T>) STORE.get(cacheKey);
    }

    /**
     * 读取数据列表，不存在时返回空列表.
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> getDataList(String cacheKey) {
        CacheData<?> cacheData = STORE.get(cacheKey);
        if (cacheData == null || cacheData.getDataList() == null) {
            return Collections.emptyList();
        }
        return (List<T>) cacheData.getDataList();
    }

    /**
     * 获取指定 cacheKey 的最新更新时间，不存在时返回 null.
     */
    public static LocalDateTime getLatestUpdateTime(String cacheKey) {
        CacheData<?> data = STORE.get(cacheKey);
        return data != null ? data.getLatestUpdateTime() : null;
    }

    /**
     * 判断指定 cacheKey 是否已加载过数据.
     */
    public static boolean isLoaded(String cacheKey) {
        return STORE.containsKey(cacheKey);
    }

    /**
     * 获取当前已加载的缓存数量.
     */
    public static int size() {
        return STORE.size();
    }

    /**
     * 获取所有已加载的 cacheKey.
     */
    public static java.util.Set<String> loadedKeys() {
        return Collections.unmodifiableSet(STORE.keySet());
    }

    /**
     * 清空所有缓存（仅用于测试或极端场景）.
     */
    public static void clear() {
        log.warn("CacheDataHolder.clear() called, dropping all caches");
        STORE.clear();
    }

    /**
     * 调试用：打印当前缓存概览.
     */
    public static String snapshot() {
        StringBuilder sb = new StringBuilder("CacheDataHolder snapshot:\n");
        for (Map.Entry<String, CacheData<?>> entry : STORE.entrySet()) {
            CacheData<?> cd = entry.getValue();
            sb.append(String.format("  [%s] records=%-5d latestUpdate=%s%n",
                    entry.getKey(),
                    cd.getDataList().size(),
                    cd.getLatestUpdateTime()));
        }
        return sb.toString();
    }
}
