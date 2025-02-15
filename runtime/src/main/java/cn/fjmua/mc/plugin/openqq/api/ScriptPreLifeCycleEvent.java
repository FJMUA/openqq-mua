package cn.fjmua.mc.plugin.openqq.api;

import cn.fjmua.mc.plugin.openqq.script.Script;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * 脚本生命周期变化事件
 * */
@Getter
public class ScriptPreLifeCycleEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Script script;
    private final Object[] args;

    public ScriptPreLifeCycleEvent(Script script, Object ...args) {
        this.script = script;
        this.args = args;
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
