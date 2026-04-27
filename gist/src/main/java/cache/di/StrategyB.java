package cache.di;

import org.springframework.stereotype.Component;

@Component
public class StrategyB implements MyStrategy {
    @Override
    public void doSomething() {
        System.out.println("执行策略 B");
    }

    @Override
    public String getType() {
        return "B";
    }
}