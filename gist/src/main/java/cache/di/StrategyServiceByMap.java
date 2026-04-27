package cache.di;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class StrategyServiceByMap {

    /**
     * KEY：bean 名称
     * VALUE：对应的实现类实例
     * Spring 自动注入全部实现
     */
    @Autowired
    private Map<String, MyStrategy> strategyMap;

    public void executeByType(String type) {
        MyStrategy strategy = strategyMap.get(type);
        if (strategy != null) {
            strategy.doSomething();
        }
    }
}
