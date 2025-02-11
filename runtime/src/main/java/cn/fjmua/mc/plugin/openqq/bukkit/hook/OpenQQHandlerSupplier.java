package cn.fjmua.mc.plugin.openqq.bukkit.hook;

import cn.byteforge.openqq.ws.event.Event;
import cn.byteforge.openqq.ws.event.EventListener;
import cn.byteforge.openqq.ws.handler.ChainHandler;
import cn.byteforge.openqq.ws.pojo.Intent;
import cn.fjmua.mc.plugin.openqq.api.OpenQQEvent;
import cn.fjmua.mc.plugin.openqq.bukkit.Bootstrap;

import java.util.function.Supplier;

public class OpenQQHandlerSupplier implements Supplier<ChainHandler> {

    @Override
    public ChainHandler get() {
        Bootstrap instance = Bootstrap.getInstance();
        return ChainHandler.defaultChainGroup(null,
                new EventListener<Event>() {
                    @Override
                    public void onEvent(Event event) {
                        new OpenQQEvent(event).call();
                    }

                    @Override
                    public Intent eventIntent() {
                        return instance.getEventIntent();
                    }
                });
    }

}
