package cache;

import cache.model.Badge;
import cache.model.Gift;

import java.util.Collections;
import java.util.List;

/**
 * 缓存读取门面 —— 对外暴露类型安全的只读接口.
 *
 * <p>调用方不应该直接操作 {@link CacheDataHolder}，
 * 而是通过此类获取业务语义明确的数据.
 */
public final class CacheAccessor {

    private CacheAccessor() {
    }

    // ====================== 勋章 ======================

    /**
     * 获取全部勋章列表.
     */
    public static List<Badge> getAllBadges() {
        return CacheDataHolder.getDataList(CacheConstant.KEY_BADGE);
    }

    /**
     * 根据 ID 获取勋章，不存在返回 null.
     */
    public static Badge getBadgeById(Long id) {
        return getAllBadges().stream()
                .filter(b -> b.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    /**
     * 勋章缓存是否已加载.
     */
    public static boolean isBadgeLoaded() {
        return CacheDataHolder.isLoaded(CacheConstant.KEY_BADGE);
    }

    // ====================== 礼包 ======================

    /**
     * 获取全部礼包列表.
     */
    public static List<Gift> getAllGifts() {
        return CacheDataHolder.getDataList(CacheConstant.KEY_GIFT);
    }

    /**
     * 根据 ID 获取礼包，不存在返回 null.
     */
    public static Gift getGiftById(Long id) {
        return getAllGifts().stream()
                .filter(g -> g.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    /**
     * 礼包缓存是否已加载.
     */
    public static boolean isGiftLoaded() {
        return CacheDataHolder.isLoaded(CacheConstant.KEY_GIFT);
    }

    // ====================== 通用 ======================

    /**
     * 按条件过滤（示例：按等级筛选勋章）.
     */
    public static List<Badge> filterBadges(java.util.function.Predicate<Badge> predicate) {
        return getAllBadges().stream()
                .filter(predicate)
                .toList();
    }

    /**
     * 按条件过滤礼包.
     */
    public static List<Gift> filterGifts(java.util.function.Predicate<Gift> predicate) {
        return getAllGifts().stream()
                .filter(predicate)
                .toList();
    }
}
