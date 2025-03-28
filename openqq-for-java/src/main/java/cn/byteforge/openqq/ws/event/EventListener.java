package cn.byteforge.openqq.ws.event;

import cn.byteforge.openqq.ws.pojo.Intent;

/**
 * 事件监听接口
 * */
public interface EventListener<T extends Event> {

    /**
     * 监听事件
     * @apiNote 本方法不维护状态，非线程安全
     * */
    void onEvent(T event);

    /**
     * 监听的事件类型 {@link Intent}
     * */
    Intent eventIntent();

    /**
     * 是否忽略被取消的事件
     * */
    default boolean ignoreCancelled() {
        return true;
    }

}
