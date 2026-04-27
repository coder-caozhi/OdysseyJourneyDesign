package event;

import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Component;

@Component
public class BaseGrowthEvent extends ApplicationEvent {
    public BaseGrowthEvent(Object source) {
        super(source);
    }
}
