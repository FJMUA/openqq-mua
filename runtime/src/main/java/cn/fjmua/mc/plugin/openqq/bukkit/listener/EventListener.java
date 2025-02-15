package cn.fjmua.mc.plugin.openqq.bukkit.listener;

import cn.fjmua.mc.plugin.openqq.api.OpenQQEvent;
import cn.fjmua.mc.plugin.openqq.api.ScriptPreLifeCycleEvent;
import cn.fjmua.mc.plugin.openqq.script.Script;
import cn.fjmua.mc.plugin.openqq.script.ScriptLifeCycleEnum;
import cn.fjmua.mc.plugin.openqq.script.ScriptManager;
import cn.fjmua.mc.plugin.openqq.util.RedisUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.TreeSet;
import java.util.function.Function;

public class EventListener implements Listener {

    private final ScriptManager manager = ScriptManager.getInstance();

    @EventHandler
    public void onOpenQQEvent(OpenQQEvent event) {
        onEventLifeCycle(event, null);
    }

    @EventHandler
    public void onLifeCycle(ScriptPreLifeCycleEvent event) {
        onEventLifeCycle(event, script ->
                event.getScript().equals(script));
    }

    @EventHandler
    public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event) {
        onEventLifeCycle(event, null);
    }

    protected void onEventLifeCycle(Event event, Function<Script, Boolean> scriptFilter) {
        TreeSet<Script> scriptTree = manager.getScriptTree();
        ScriptLifeCycleEnum eventLifeCycleEnum = ScriptLifeCycleEnum.EVENT;
        for (Script script : scriptTree) {
            if (scriptFilter != null && scriptFilter.apply(script)) {
                continue;
            }
            eventLifeCycleEnum.onLifeCycle(script, event);
        }
    }

}
