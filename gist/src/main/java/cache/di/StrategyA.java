package cache.di;

import org.springframework.stereotype.Component;

@Component
public class StrategyA implements MyStrategy {
    @Override
    public void doSomething() {
        System.out.println("执行策略 A");
    }

    @Override
    public String getType() {
        return "A";
    }
}