package person.shw.springboot.plugin.eventqueue.producer;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.TimeoutException;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.EventHandlerGroup;
import com.lmax.disruptor.dsl.ProducerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import person.shw.springboot.plugin.eventqueue.event.EventWrapper;
import person.shw.springboot.plugin.eventqueue.event.EventWrapperFactory;
import person.shw.springboot.plugin.eventqueue.event.EventWrapperTranslator;
import person.shw.springboot.plugin.eventqueue.handler.ClearEventHandler;
import person.shw.springboot.plugin.eventqueue.handler.EventExceptionHandler;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author shihaowei
 * @date 2020/9/30 3:00 下午
 */
public abstract class AbstractEventProducer<T> implements EventProducer<T> {

    private static  final Logger LOG = LoggerFactory.getLogger(AbstractEventProducer.class);

    private ThreadFactory threadFactory;
    private EventWrapperFactory<T> eventFactory;
    private EventWrapperTranslator<T> eventTranslator;
    private EventExceptionHandler<T> exceptionHandler;
    private ClearEventHandler<T> clearEventHandler;
    private RingBuffer<EventWrapper<T>> ringBuffer;

    private final WaitStrategy waitStrategy;
    private final ProducerType producerType;
    private final int ringBufferSize;
    private final String name;
    private final AtomicBoolean ready;
    private final CopyOnWriteArrayList<T> prePublicDataList;

    protected Disruptor<EventWrapper<T>> disruptor;

    public AbstractEventProducer(String name) {
        this(name,64 * 1024,ProducerType.MULTI,new BlockingWaitStrategy());
    }

    public AbstractEventProducer( String name, int ringBufferSize, ProducerType producerType, WaitStrategy waitStrategy) {
        this.name = name;
        this.ringBufferSize = ringBufferSize;
        this.producerType = producerType;
        this.waitStrategy = waitStrategy;
        this.ready = new AtomicBoolean(false);
        prePublicDataList = new CopyOnWriteArrayList<>();
    }

    public String getName() {
        return name;
    }

    protected ThreadFactory initThreadFactory(){
        return new ThreadFactoryBuilder().setNameFormat(name+"-t%d").build();
    }

    protected EventWrapperFactory initEventFactory(){
        return new EventWrapperFactory();
    }

    protected EventWrapperTranslator<T> initEventTranslator() {
        return new EventWrapperTranslator<>();
    }

    protected EventExceptionHandler<T> initExceptionHandler() {
        return new EventExceptionHandler<>();
    }

    protected ClearEventHandler<T> initClearEventHandler() {
        return new ClearEventHandler<>();
    }

    protected void init(){
        threadFactory = initThreadFactory();
        eventFactory = initEventFactory();
        eventTranslator = initEventTranslator();
        disruptor = new Disruptor<>(eventFactory, ringBufferSize, threadFactory, producerType, waitStrategy);

        exceptionHandler = initExceptionHandler();

        if (exceptionHandler != null){
            this.disruptor.setDefaultExceptionHandler(exceptionHandler);
        }

        EventHandlerGroup<EventWrapper<T>> handlerGroup = initEventHandlerGroup(this.disruptor);

        clearEventHandler = initClearEventHandler();
        if (clearEventHandler != null) {
            handlerGroup.then(clearEventHandler);
        }
    }

    @Override
    public void start() {
        LOG.info("{} try to start...", this.getClass().getSimpleName());

        init();

        ringBuffer = disruptor.start();
        ready.set(true);

        for (T data : prePublicDataList) {
            ringBuffer.publishEvent(eventTranslator, data);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    @Override
    public void publish(T data) {
        if (!ready.get()) {
            prePublicDataList.add(data);
            return;
        }
        ringBuffer.publishEvent(eventTranslator, data);
    }

    @Override
    public void shutdown() {
        //TODO 处理未消费的
        LOG.info("{} try to shutdown...", this.getClass().getSimpleName());
        try {
            disruptor.shutdown(20L, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            LOG.error("{} shutdown timeout", this.getClass().getSimpleName(), e);
        }
    }
}
