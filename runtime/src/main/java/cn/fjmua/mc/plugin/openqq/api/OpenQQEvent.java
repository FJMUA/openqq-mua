package cn.fjmua.mc.plugin.openqq.api;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class OpenQQEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    @Getter
    private final cn.byteforge.openqq.ws.event.Event originEvent;

    public OpenQQEvent(cn.byteforge.openqq.ws.event.Event originEvent) {
        this.originEvent = originEvent;
    }

    /**
     * 广播事件
     */
    public void call() {
        Bukkit.getServer().getPluginManager().callEvent(this);
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
