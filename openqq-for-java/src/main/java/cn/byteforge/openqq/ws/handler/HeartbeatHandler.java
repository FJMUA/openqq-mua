package cn.byteforge.openqq.ws.handler;

import cn.byteforge.openqq.task.HeartbeatRunnable;
import cn.byteforge.openqq.task.ThreadPoolManager;
import cn.byteforge.openqq.ws.event.Event;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 心跳处理
 * */
@Slf4j
public class HeartbeatHandler extends ChainHandler {

    private final ScheduledExecutorService executor = ThreadPoolManager.newSingleThreadScheduledExecutor("心跳-" + System.currentTimeMillis());
    private ScheduledFuture<?> heartbeatTask;

    // Event -> Event
    @Override
    protected Object doHandle(Object o) {
        Event event = (Event) o;
        switch (event.getOpcode()) {
            case HELLO: {
                if (heartbeatTask != null) {
                    heartbeatTask.cancel(true);
                    log.info("Duplicate heartbeat thread detected, is this connection reconnect ?");
                }
                long interval = event.getJson().getAsJsonObject("d").get("heartbeat_interval").getAsLong();
                // ScheduledExecutor leaves 20% of the interval to avoid errors
                interval -= (interval / 10) * 2;
                // start to heartbeat
                heartbeatTask = executor.scheduleAtFixedRate(new HeartbeatRunnable(getUuid(), getContext()), interval, interval, TimeUnit.MILLISECONDS);
                log.debug("Heartbeat thread start with interval {}ms", interval);
                return null;
            }
            case HEARTBEAT: {
                log.debug("Received HEARTBEAT");
                return null;
            }
            case HEARTBEAT_ACK: {
                log.debug("Received HEARTBEAT_ACK");
                return null;
            }
        }
        return event;
    }

    @Override
    public void onClose() {
        executor.shutdownNow();
    }

}
