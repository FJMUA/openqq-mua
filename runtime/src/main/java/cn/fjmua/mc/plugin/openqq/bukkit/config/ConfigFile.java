package cn.fjmua.mc.plugin.openqq.bukkit.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

@Slf4j
public class ConfigFile {
    private final String name;
    private final Plugin instance;
    private File file;
    @Getter
    private volatile FileConfiguration config;

    public ConfigFile(String name, Plugin instance) {
        this.name = name;
        this.instance = instance;
        this.config = this.load();
    }

    public void save() {
        try {
            this.config.save(this.file);
        } catch (IOException e) {
            log.error("Config file saved failed:", e);
        }
        this.config = this.load();
    }

    /**
     * 更新并保存配置文件
     */
    public void update(String yaml) {
        try {
            config.loadFromString(yaml);
        } catch (InvalidConfigurationException e) {
            log.error("Config file load from string failed:", e);
        }
        save();
    }

    private FileConfiguration load() {
        File file = new File(this.instance.getDataFolder(), this.name);
        if (!file.exists()) {
            this.instance.saveResource(this.name, false);
        }

        FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        this.file = file;
        return configuration;
    }

    public void reload() {
        this.config = this.load();
    }

}
