package cache.di;

public interface MyStrategy {
    /**
     * 每个实现类自己的业务方法
     */
    void doSomething();

    /**
     * 用来标识类型（Map注入时常用）
     */
    String getType();
}