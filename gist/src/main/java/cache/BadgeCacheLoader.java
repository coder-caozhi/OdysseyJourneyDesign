package cache;

import cache.model.Badge;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 勋章缓存加载器 —— 调用管理服务的勋章接口.
 *
 * <p>管理服务勋章接口返回：
 * <ul>
 *   <li>勋章表全部记录</li>
 *   <li>所有记录中最新的 {@code updateTime}</li>
 * </ul>
 *
 * <p>生产环境中，{@link #fetchFromRemote()} 内部应替换为实际的 RPC 调用，
 * 例如 Dubbo {@code @Reference}、gRPC Stub、或 Feign 客户端.
 */
@Slf4j
@Component
public class BadgeCacheLoader extends AbstractCacheLoader<Badge> {

    @Override
    public String getCacheKey() {
        return CacheConstant.KEY_BADGE;
    }

    /**
     * 【示例】模拟 RPC 调用管理服务勋章接口.
     *
     * <p>生产环境替换为：
     * <pre>{@code
     * BadgeRpcResponse resp = badgeServiceClient.getAllBadges();
     * return CacheData.of(resp.getDataList(), resp.getLatestUpdateTime());
     * }</pre>
     */
    @Override
    protected CacheData<Badge> fetchFromRemote() {
        // ============ 模拟 RPC 调用 ============
        log.debug("BadgeCacheLoader calling management-service /api/badge/list ...");

        List<Badge> mockList = new ArrayList<>();
        mockList.add(Badge.builder()
                .id(1L)
                .name("探险家勋章")
                .iconUrl("https://cdn.example.com/badge/explorer.png")
                .requiredLevel(5)
                .description("完成10次探险任务获得")
                .updateTime(LocalDateTime.now().minusDays(1))
                .build());
        mockList.add(Badge.builder()
                .id(2L)
                .name("收藏大师")
                .iconUrl("https://cdn.example.com/badge/collector.png")
                .requiredLevel(10)
                .description("收集100件藏品获得")
                .updateTime(LocalDateTime.now().minusHours(2))
                .build());
        mockList.add(Badge.builder()
                .id(3L)
                .name("社交达人")
                .iconUrl("https://cdn.example.com/badge/social.png")
                .requiredLevel(3)
                .description("添加50位好友获得")
                .updateTime(LocalDateTime.now())
                .build());

        // latestUpdateTime = 所有记录中最新的 updateTime
        LocalDateTime latest = LocalDateTime.now();

        return CacheData.of(mockList, latest);
    }
}
