package cache;

/**
 * 缓存服务接口 —— 缓存模块对外的操作契约.
 */
public interface ICacheService {

    /**
     * 刷新全部缓存（全量、同步）.
     *
     * @return 实际发生更新的缓存数量
     */
    int refreshAll();

    /**
     * 刷新指定缓存.
     *
     * @param cacheKey 缓存标识
     * @return true 表示数据发生更新
     */
    boolean refresh(String cacheKey);

    /**
     * 缓存模块是否已完成首次初始化加载.
     */
    boolean isInitialized();

    /**
     * 获取缓存模块状态快照（调试用）.
     */
    String snapshot();
}
