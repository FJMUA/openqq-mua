package cn.fjmua.mc.plugin.openqq.bukkit;

import cn.byteforge.openqq.ws.BotContext;
import cn.byteforge.openqq.ws.QQConnection;
import cn.byteforge.openqq.ws.pojo.Intent;
import cn.fjmua.mc.plugin.openqq.bukkit.command.CommandTabExecutor;
import cn.fjmua.mc.plugin.openqq.bukkit.config.ConfigFile;
import cn.fjmua.mc.plugin.openqq.bukkit.hook.OpenQQHandlerSupplier;
import cn.fjmua.mc.plugin.openqq.bukkit.hook.OpenQQSessionFunction;
import cn.fjmua.mc.plugin.openqq.bukkit.listener.EventListener;
import cn.fjmua.mc.plugin.openqq.script.ScriptManager;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter
@Slf4j
public class Bootstrap extends JavaPlugin {

    @Getter
    private static Bootstrap instance;

    private ExecutorService executor;
    private ConfigFile configFile;

    private Intent eventIntent;
    private BotContext botContext;

    @Override
    public void onLoad() {
        instance = this;
        new ConfigFile("redis.properties", instance);
        configFile = new ConfigFile("config.yml", instance);
        FileConfiguration config = configFile.getConfig();
        executor = Executors.newFixedThreadPool(20);
        botContext = BotContext.create(config.getString("openqq.app-id"), config.getString("openqq.secret"), executor);
        eventIntent = Intent.register().withAll().done();
        QQConnection.connect(
                botContext,
                new OpenQQHandlerSupplier(),
                new OpenQQSessionFunction(),
                uuid -> log.info("OpenQQ 链接已建立, uuid: {}", uuid)
        );
    }

    @Override
    public void onEnable() {
        ScriptManager.getInstance().clearAndLoadScripts();
        getServer().getPluginManager().registerEvents(new EventListener(), instance);
        CommandTabExecutor tabExecutor = new CommandTabExecutor();
        Optional.ofNullable(getServer().getPluginCommand("OpenQQ-MUA")).
                ifPresent(command -> {
                    command.setExecutor(tabExecutor);
                    command.setTabCompleter(tabExecutor);
                });
    }

    @Override
    public void onDisable() {
        instance = null;
    }

}
