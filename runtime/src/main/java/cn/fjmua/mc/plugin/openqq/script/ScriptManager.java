package cn.fjmua.mc.plugin.openqq.script;

import cn.fjmua.mc.plugin.openqq.api.script.PluginInfo;
import cn.fjmua.mc.plugin.openqq.bukkit.Bootstrap;
import cn.fjmua.mc.plugin.openqq.util.FileUtil;
import cn.fjmua.mc.plugin.openqq.util.ParseUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.graalvm.polyglot.Value;

import java.io.File;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Getter
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
        Bootstrap instance = Bootstrap.getInstance();
        doClearAndLoadScripts(
                () -> new File(instance.getDataFolder(), "plugins"),
                templateFilePath -> instance.saveResource(templateFilePath, false)
        );
        log.info("脚本加载完成，共 {} 个", scriptTree.size());
        for (Script script : scriptTree) {
            PluginInfo pluginInfo = script.getPluginInfo();
            // => (优先级: 0)[测试脚本-20240201] - 已加载
            log.info("=> (优先级: {})[{}-{}] - {}", pluginInfo.getPriority(), pluginInfo.getName(), pluginInfo.getVersion(), script.getStatus().description());
        }
    }

    /**
     * @param pluginFolderSupplier 提供脚本文件夹实例
     * @param templateFilePathConsumer 消费模板脚本路径，生成文件
     * */
    protected void doClearAndLoadScripts(Supplier<File> pluginFolderSupplier, Consumer<String> templateFilePathConsumer) {
        onLifeCycle(ScriptLifeCycleEnum.DESTROY);
        scriptTree.clear();
        GraalvmScriptAgent.getScriptContextMap().clear();

        File pluginFolder = pluginFolderSupplier.get();
        if (!pluginFolder.exists()) {
            templateFilePathConsumer.accept("plugins/plugin.template.js");
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
            PluginInfo pluginInfo = new PluginInfo();
            try {
                pluginInfo.setName(scriptName);
                script.setFile(scriptFile);
                script.setContent(FileUtil.readContent(scriptFile));
                script.setStatus(ScriptStatus.LOADED_UNKNOWN);
                scriptTree.add(script);

                Value pluginInfoValue = GraalvmScriptAgent.getField("pluginInfo", script);
                if (pluginInfoValue != null) {
                    pluginInfo = ParseUtil.toObject(pluginInfoValue, PluginInfo.class);
                }
                script.setStatus(ScriptStatus.LOADED);
            } catch (Exception e) {
                log.error("加载脚本 {} 出错", script.getPluginInfo().getName(), e);
            }
            script.setPluginInfo(pluginInfo);
        }
        onLifeCycle(ScriptLifeCycleEnum.INIT);
    }

    protected void onLifeCycle(ScriptLifeCycleEnum lifeCycleEnum) {
        for (Script script : scriptTree) {
            lifeCycleEnum.onLifeCycle(script);
        }
    }

    public static ScriptManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final ScriptManager INSTANCE = new ScriptManager();
    }

}
