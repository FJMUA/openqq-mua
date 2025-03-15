package cn.byteforge.openqq.task;

import cn.byteforge.openqq.util.Maps;
import cn.byteforge.openqq.ws.BotContext;
import cn.byteforge.openqq.ws.WebSocketAPI;
import cn.byteforge.openqq.ws.entity.enumerate.OpCode;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Heartbeat with fixed interval
 * */
@Slf4j
public class HeartbeatRunnable implements Runnable {

    private final UUID uuid;
    private final BotContext context;

    public HeartbeatRunnable(UUID uuid, BotContext context) {
        this.uuid = uuid;
        this.context = context;
    }

    @Override
    public void run() {
        // first d is null
        try {
            log.debug("Try to start a heartbeat");
            WebSocketAPI.send(Maps.of(
                    "op", OpCode.HEARTBEAT.getCode(),
                    "d", context.getReceivedSeqMap().get(uuid)
            ), uuid, WebSocketAPI.NO_NEED_CALLBACK, context).get(5, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            log.error("Heartbeat thread interrupted", e);
        } catch (Exception e) {
            log.error("Heartbeat thread error", e);
        }
    }

}
