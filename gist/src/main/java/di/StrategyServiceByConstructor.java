package di;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StrategyServiceByConstructor {
    @Autowired
    private final List<MyStrategy> strategyList;

    /**
     * 构造器自动承接 List 或 Map
     */
    public StrategyServiceByConstructor(List<MyStrategy> strategyList){
        this.strategyList = strategyList;
    }

    public void doBusiness() {
        // 直接使用
        strategyList.forEach(MyStrategy::doSomething);
    }
}
