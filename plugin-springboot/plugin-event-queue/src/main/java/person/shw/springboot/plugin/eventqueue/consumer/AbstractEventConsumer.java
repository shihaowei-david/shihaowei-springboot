package person.shw.springboot.plugin.eventqueue.consumer;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;
import person.shw.springboot.plugin.eventqueue.event.EventWrapper;

/**
 * @author shihaowei
 * @date 2020/9/30 2:44 下午
 */
public abstract class AbstractEventConsumer<T> implements WorkHandler<EventWrapper<T>>, EventHandler<EventWrapper<T>>,EventConsumer<T> {

    private final String name;

    public AbstractEventConsumer(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public void onEvent(EventWrapper<T> event) throws Exception {
        T data = event.getData();
        if (data != null) {
            process(data);
        }
    }

    @Override
    public void onEvent(EventWrapper<T> event, long sequence, boolean endOfBatch) throws Exception {
        T data = event.getData();
        if (data != null){
            process(data,sequence,endOfBatch);
        }
    }
}
