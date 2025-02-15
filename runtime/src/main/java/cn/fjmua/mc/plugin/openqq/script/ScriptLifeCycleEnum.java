package cn.fjmua.mc.plugin.openqq.script;

import cn.fjmua.mc.plugin.openqq.api.ScriptPreLifeCycleEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

/**
 * 脚本生命周期枚举
 * */
@AllArgsConstructor
@Getter
public enum ScriptLifeCycleEnum {

    /**
     * 初始化
     * */
    INIT("init"),

    /**
     * 接收事件
     * */
    EVENT("event"),

    /**
     * 销毁
     * */
    DESTROY("destroy"),

    ;

    private final String mapperKey;

    public void onLifeCycle(Script script, Object ...args) {
        Map<String, String> invokedMapper = script.getPluginInfo().getInvokedMapper();
        if (invokedMapper == null || invokedMapper.isEmpty()) {
            return;
        }
        String funcs = invokedMapper.get(this.getMapperKey());
        if (funcs == null || funcs.isEmpty()) {
            return;
        }
        new ScriptPreLifeCycleEvent(script, args).call();
        for (String initFunc : funcs.split(",")) {
            if (initFunc == null || initFunc.isEmpty()) {
                continue;
            }
            GraalvmScriptAgent.runFunc(initFunc, script, args);
        }
    }

}
