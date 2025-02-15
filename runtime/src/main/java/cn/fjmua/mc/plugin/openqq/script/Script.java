package cn.fjmua.mc.plugin.openqq.script;

import cn.fjmua.mc.plugin.openqq.api.script.PluginInfo;
import lombok.Data;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Data
public class Script implements Comparable<Script> {

    /**
     * 插件信息
     * */
    private PluginInfo pluginInfo;

    /**
     * 脚本参数
     * @apiNote 仅单向传递 java -> js
     * */
    private Map<String, Object> bindingsMap = new HashMap<>();

    /**
     * 脚本文件实例
     * */
    private File file;

    /**
     * 脚本内容
     * */
    private String content;

    /**
     * 脚本加载状态
     * */
    private ScriptStatus status = ScriptStatus.INIT;

    @Override
    public int compareTo(Script script) {
        return pluginInfo.getPriority().compareTo(script.pluginInfo.getPriority());
    }

}
