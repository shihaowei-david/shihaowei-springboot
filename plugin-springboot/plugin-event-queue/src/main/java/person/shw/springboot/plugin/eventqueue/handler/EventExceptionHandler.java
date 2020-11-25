package person.shw.springboot.plugin.eventqueue.handler;

import com.lmax.disruptor.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import person.shw.springboot.plugin.eventqueue.event.EventWrapper;

/**
 * @author shihaowei
 * @date 2020/9/30 3:11 下午
 */
public class EventExceptionHandler<T> implements ExceptionHandler<EventWrapper<T>> {

    private static final Logger LOG = LoggerFactory.getLogger(EventExceptionHandler.class);

    @Override
    public void handleEventException(Throwable ex, long sequence, EventWrapper<T> event) {
        LOG.error("event handler process error：", ex);
    }

    @Override
    public void handleOnStartException(Throwable ex) {
        LOG.error("event handler start error：", ex);
    }

    @Override
    public void handleOnShutdownException(Throwable ex) {
        LOG.error("event handler shutdown error：", ex);
    }
}
