package cn.fjmua.mc.plugin.openqq.bukkit.hook;

import cn.byteforge.openqq.ws.WebSocketAPI;
import cn.byteforge.openqq.ws.pojo.Session;
import cn.fjmua.mc.plugin.openqq.bukkit.Bootstrap;

import java.util.UUID;
import java.util.function.Function;

public class OpenQQSessionFunction implements Function<UUID, Session> {

    @Override
    public Session apply(UUID uuid) {
        Bootstrap instance = Bootstrap.getInstance();
        return WebSocketAPI.newStandaloneSession(
                instance.getEventIntent(),
                uuid,
                null,
                instance.getBotContext()
        );
    }

}
