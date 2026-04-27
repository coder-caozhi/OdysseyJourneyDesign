package cache.di;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class StrategyServiceByConcstructor {
    private final List<MyStrategy> strategyList;

    /**
     * 构造器自动承接 List 或 Map
     */
    public StrategyServiceByConcstructor(List<MyStrategy> strategyList){
        this.strategyList = strategyList;
    }

    public void doBusiness() {
        // 直接使用
        strategyList.forEach(MyStrategy::doSomething);
    }
}
