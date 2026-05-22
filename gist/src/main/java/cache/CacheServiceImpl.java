package cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 缓存服务实现 —— 封装 Loader 的调用入口.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CacheServiceImpl implements ICacheService {

    /** Spring 自动收集所有 AbstractCacheLoader 子类 Bean */
    private final List<AbstractCacheLoader<?>> cacheLoaders;

    @Override
    public int refreshAll() {
        int updated = 0;
        for (AbstractCacheLoader<?> loader : cacheLoaders) {
            if (loader.load()) {
                updated++;
            }
        }
        log.info("CacheServiceImpl.refreshAll() done, {}/{} loaders updated",
                updated, cacheLoaders.size());
        return updated;
    }

    @Override
    public boolean refresh(String cacheKey) {
        return cacheLoaders.stream()
                .filter(l -> l.getCacheKey().equals(cacheKey))
                .findFirst()
                .map(AbstractCacheLoader::load)
                .orElse(false);
    }

    @Override
    public boolean isInitialized() {
        // 所有声明的 Loader 对应的缓存都已加载过数据
        return cacheLoaders.stream()
                .allMatch(l -> CacheDataHolder.isLoaded(l.getCacheKey()));
    }

    @Override
    public String snapshot() {
        return CacheDataHolder.snapshot();
    }
}
