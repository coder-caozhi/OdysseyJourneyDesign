package event;

public interface IHanlder<T extends BaseGrowthEvent> {
     void handleEvent(T event);
}
