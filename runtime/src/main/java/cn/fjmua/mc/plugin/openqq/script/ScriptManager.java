package cn.fjmua.mc.plugin.openqq.script;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class ScriptManager {

    public void run() {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");

    }

}
