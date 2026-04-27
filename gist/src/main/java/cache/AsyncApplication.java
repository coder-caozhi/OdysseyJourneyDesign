package cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@SpringBootApplication
public class AsyncApplication implements CommandLineRunner {

    public static void main(String[] args) {
        // 2. 启动 Spring 容器
        SpringApplication.run(AsyncApplication.class, args);
    }

    @Autowired
    private List<AbstractCacheLoader> cacheLoaders;


    @Override
    public void run(String... args) throws Exception {
        // 4. 这里的代码是在 Spring 容器完全启动后执行的
        System.out.println("开始执行异步任务，共发现 " + cacheLoaders.size() + " 个加载器");

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (AbstractCacheLoader cacheLoader : cacheLoaders) {
            futures.add(CompletableFuture.runAsync(() -> {
                // 这里的 cacheLoader 是完整的 Spring Bean，内部的依赖也是注入好的
                cacheLoader.load(); // 假设 AbstractCacheLoader 有个 load 方法
                System.out.println("异步任务完成：" + cacheLoader.getClass().getSimpleName());
            }));
        }

        // 等待所有任务完成
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        System.out.println("所有任务结束");
    }
}
