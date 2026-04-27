package cache.di;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class StrategyService {

    /**
     * 自动注入所有 MyStrategy 的实现类
     * Spring 会自动把所有 @Component 实现类装进数组里
     */
    @Autowired
    private MyStrategy[] strategyArray;

    /**
     * 也可以用 List
     */
    @Autowired
    private List<MyStrategy> strategyList;

    public void executeAll() {
        for (MyStrategy strategy : strategyArray) {
            strategy.doSomething();
        }
    }
}