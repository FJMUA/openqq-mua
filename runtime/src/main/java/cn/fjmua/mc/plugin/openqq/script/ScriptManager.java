package cn.fjmua.mc.plugin.openqq.script;

import cn.fjmua.mc.plugin.openqq.api.script.PluginInfo;
import cn.fjmua.mc.plugin.openqq.bukkit.Bootstrap;
import cn.fjmua.mc.plugin.openqq.util.FileUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.TreeSet;

@Slf4j
public class ScriptManager {

    private final TreeSet<Script> scriptTree;

    public ScriptManager() {
        this.scriptTree = new TreeSet<>();
    }

    /**
     * 加载脚本文件
     */
    public void clearAndLoadScripts() {
        log.info("正在加载脚本文件");
        doClearAndLoadScripts();
        log.info("脚本加载完成，共 {} 个", scriptTree.size());
        for (Script script : scriptTree) {
            // => (优先级: 0)[测试脚本-20240201] - 已加载
            log.info("=> (优先级: {})[{}-{}] - {}", script.getPriority(), script.getName(), script.getVersion(), script.getStatus().getDescription());
        }
    }

    protected void doClearAndLoadScripts() {
        scriptTree.clear();
        JavaScriptAgent.getCompiledScripts().clear();

        Bootstrap instance = Bootstrap.getInstance();
        File pluginFolder = new File(instance.getDataFolder(), "plugins");
        if (!pluginFolder.exists()) {
            instance.saveResource("plugins/plugin.template.js", false);
        }
        File[] listFiles = pluginFolder.listFiles();
        if (listFiles == null) {
            return;
        }
        for (File scriptFile : listFiles) {
            if (scriptFile.isDirectory()) {
                continue;
            }
            String scriptName = scriptFile.getName();
            if (!scriptName.endsWith(".js")) {
                continue;
            }

            Script script = new Script();
            try {
                script.setName(scriptName);
                script.setFile(scriptFile);
                script.setContent(FileUtil.readContent(scriptFile));
                script.setStatus(Script.Status.LOADED_UNKNOWN);
                scriptTree.add(script);

                // optional
                PluginInfo pluginInfo = JavaScriptAgent.runFunc("getPluginInfo", script, PluginInfo.class);
                script.setName(pluginInfo.getName());
                script.setAuthor(pluginInfo.getAuthor());
                script.setVersion(pluginInfo.getVersion());
                script.setPriority(pluginInfo.getPriority());
                script.setStatus(Script.Status.LOADED);
            } catch (NoSuchMethodException ignored) {
                // do noting TODO debug
                log.error("Test error:", ignored);
            } catch (Exception e) {
                log.error("加载脚本 {} 出错", script.getName(), e);
            }
        }
    }

    public static ScriptManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final ScriptManager INSTANCE = new ScriptManager();
    }

}
