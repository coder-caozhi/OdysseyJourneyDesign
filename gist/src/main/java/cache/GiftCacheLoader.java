package cache;

import cache.model.Gift;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 礼包缓存加载器 —— 调用管理服务的礼包接口.
 *
 * <p>管理服务礼包接口返回：
 * <ul>
 *   <li>礼包表全部记录</li>
 *   <li>所有记录中最新的 {@code updateTime}</li>
 * </ul>
 */
@Slf4j
@Component
public class GiftCacheLoader extends AbstractCacheLoader<Gift> {

    @Override
    public String getCacheKey() {
        return CacheConstant.KEY_GIFT;
    }

    /**
     * 【示例】模拟 RPC 调用管理服务礼包接口.
     *
     * <p>生产环境替换为：
     * <pre>{@code
     * GiftRpcResponse resp = giftServiceClient.getAllGifts();
     * return CacheData.of(resp.getDataList(), resp.getLatestUpdateTime());
     * }</pre>
     */
    @Override
    protected CacheData<Gift> fetchFromRemote() {
        // ============ 模拟 RPC 调用 ============
        log.debug("GiftCacheLoader calling management-service /api/gift/list ...");

        List<Gift> mockList = new ArrayList<>();
        mockList.add(Gift.builder()
                .id(101L)
                .name("新手礼包")
                .imageUrl("https://cdn.example.com/gift/beginner.png")
                .price(0)
                .stock(9999)
                .description("注册即送的新手福利")
                .updateTime(LocalDateTime.now().minusDays(3))
                .build());
        mockList.add(Gift.builder()
                .id(102L)
                .name("钻石月卡")
                .imageUrl("https://cdn.example.com/gift/diamond_monthly.png")
                .price(30)
                .stock(5000)
                .description("每日领取100钻石，持续30天")
                .updateTime(LocalDateTime.now().minusHours(6))
                .build());
        mockList.add(Gift.builder()
                .id(103L)
                .name("限时特惠箱")
                .imageUrl("https://cdn.example.com/gift/limited_box.png")
                .price(128)
                .stock(200)
                .description("包含稀有道具的概率宝箱")
                .updateTime(LocalDateTime.now().minusMinutes(30))
                .build());

        // latestUpdateTime = 所有记录中最新的 updateTime
        LocalDateTime latest = LocalDateTime.now().minusMinutes(30);

        return CacheData.of(mockList, latest);
    }
}
