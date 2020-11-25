package person.shw.springboot.plugin.eventqueue.handler;

import com.lmax.disruptor.EventHandler;
import person.shw.springboot.plugin.eventqueue.event.EventWrapper;

/**
 * @author shihaowei
 * @date 2020/9/30 3:10 下午
 */
public class ClearEventHandler<T> implements EventHandler<EventWrapper<T>> {
    @Override
    public void onEvent(EventWrapper<T> event, long sequence, boolean endOfBatch) throws Exception {
        event.setData(null);
    }
}
