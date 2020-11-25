package person.shw.springboot.plugin.eventqueue.consumer;

/**
 * @author shihaowei
 * @date 2020/9/30 2:31 下午
 */
public interface EventConsumer<T> {

    default void process(T obj, long sequence, boolean endOfBatch) throws Exception {
        process(obj);
    }

    void process(T obj) throws Exception;
}
