package cache;

/**
 * 缓存模块常量 —— 缓存 Key、定时刷新间隔等.
 */
public final class CacheConstant {

    private CacheConstant() {
    }

    // ======================== 缓存 Key ========================

    /** 勋章缓存 */
    public static final String KEY_BADGE = "cache:badge";

    /** 礼包缓存 */
    public static final String KEY_GIFT = "cache:gift";

    // ======================== 调度参数 ========================

    /** 定时刷新间隔（分钟） */
    public static final long REFRESH_INTERVAL_MINUTES = 5;

    /** 定时刷新初始延迟（秒）—— 启动后等多久开始第一次定时刷新 */
    public static final long REFRESH_INITIAL_DELAY_SECONDS = 60;

    /** 单次 RPC 调用超时（秒） */
    public static final long RPC_TIMEOUT_SECONDS = 10;
}
