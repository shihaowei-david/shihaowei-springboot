package person.shw.springboot.plugin.eventqueue.event;

import com.lmax.disruptor.EventTranslatorOneArg;

/**
 * @author shihaowei
 * @date 2020/9/30 2:39 下午
 */
public class EventWrapperTranslator<T> implements EventTranslatorOneArg<EventWrapper<T>,T> {

    @Override
    public void translateTo(EventWrapper<T> event, long sequence, T obj) {
        event.setData(obj);
    }
}
