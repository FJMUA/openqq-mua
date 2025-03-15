package script;

import cn.fjmua.mc.plugin.openqq.api.script.PluginInfo;
import cn.fjmua.mc.plugin.openqq.script.GraalvmScriptAgent;
import cn.fjmua.mc.plugin.openqq.script.Script;
import cn.fjmua.mc.plugin.openqq.script.ScriptLifeCycleEnum;
import cn.fjmua.mc.plugin.openqq.script.ScriptStatus;
import cn.fjmua.mc.plugin.openqq.util.FileUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.TreeSet;

public class ScriptTests {

    private static final TreeSet<Script> scriptTreeSet = new TreeSet<>();

    @Test
    public void test() {
        for (Script script : scriptTreeSet) {
//            ScriptLifeCycleEnum.INIT.onLifeCycle(script);
            ScriptLifeCycleEnum.EVENT.onLifeCycle(script);
        }
    }

    @BeforeAll
    public static void testLoadScripts() throws Exception {
        GraalvmScriptAgent.getScriptContextMap().clear();

        File pluginFolder = new File("src/main/resources/plugins");
        if (!pluginFolder.exists()) {
            throw new FileNotFoundException();
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
                script.setFile(scriptFile);
                script.setContent(FileUtil.readContent(scriptFile));
                script.setStatus(ScriptStatus.LOADED_UNKNOWN);

                // optional
//                script.getBindingsMap().put("global", script);
                PluginInfo pluginInfo = GraalvmScriptAgent.getField("pluginInfo", script, PluginInfo.class);
                script.setPluginInfo(pluginInfo);
                System.out.println("pluginInfo: " + pluginInfo);
                scriptTreeSet.add(script);
            } catch (Exception e) {
                e.printStackTrace();
            }
            break;
        }
    }

}
