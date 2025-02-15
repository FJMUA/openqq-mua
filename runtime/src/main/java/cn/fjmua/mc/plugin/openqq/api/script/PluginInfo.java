package cn.fjmua.mc.plugin.openqq.api.script;

import lombok.Data;

import java.util.Map;

/**
 * 插件基础信息
 */
@Data
public class PluginInfo {

    /**
     * 脚本名称
     *
     * @apiNote 默认为文件名
     */
    private String name;

    /**
     * 脚本版本
     *
     * @apiNote 默认 unknown
     */
    private String version = "unknown";

    /**
     * 脚本作者
     *
     * @apiNote 默认 unknown
     */
    private String author = "unknown";

    /**
     * 优先级
     *
     * @apiNote int 类型，升序排序，越小越先加载，默认 100
     */
    private Integer priority = 100;

    /**
     * 注册方法映射
     * */
    private Map<String, String> invokedMapper;

}
