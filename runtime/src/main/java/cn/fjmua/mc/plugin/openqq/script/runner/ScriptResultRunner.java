package cn.fjmua.mc.plugin.openqq.script.runner;

import cn.fjmua.mc.plugin.openqq.script.ScriptParams;
import lombok.extern.slf4j.Slf4j;

import javax.script.ScriptException;
import java.util.concurrent.Callable;

@Slf4j
public class ScriptResultRunner implements Callable<Object> {

    private final ScriptParams params;

    public ScriptResultRunner(ScriptParams params) {
        this.params = params;
    }

    public Object call() {
        try {
            return params.getEngine().eval(params.getContent());
        } catch (ScriptException e) {
            log.error("Script result failed to run:", e);
            return null;
        }
    }

}
