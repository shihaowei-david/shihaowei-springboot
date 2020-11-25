package person.shw.springboot.plugin.eventqueue.event;

/**
 * @author shihaowei
 * @date 2020/9/30 2:34 下午
 */
public class EventWrapper<T> {

    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
