package person.shw.springboot.plugin.eventqueue.producer;

import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.EventHandlerGroup;
import person.shw.springboot.plugin.eventqueue.event.EventWrapper;

/**
 * @author shihaowei
 * @date 2020/9/30 2:57 下午
 */
public interface EventProducer<T> {

    EventHandlerGroup<EventWrapper<T>> initEventHandlerGroup(Disruptor<EventWrapper<T>> disruptor);

    void start();

    void publish(T data);

    void shutdown();
}
