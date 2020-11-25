package person.shw.springboot.plugin.eventqueue.event;

import com.lmax.disruptor.EventFactory;

/**
 * @author shihaowei
 * @date 2020/9/30 2:37 下午
 */
public class EventWrapperFactory<T> implements EventFactory<EventWrapper<T>> {

    @Override
    public EventWrapper<T> newInstance() {
        return new EventWrapper<>();
    }
}
