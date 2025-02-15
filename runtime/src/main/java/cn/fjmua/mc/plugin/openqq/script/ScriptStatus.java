package cn.fjmua.mc.plugin.openqq.script;

/**
 * 脚本状态枚举
 *
 * @param description 状态描述
 * @param stage       状态阶段值
 */
public record ScriptStatus(String description, Integer stage) {

    public static final Integer INIT_STAGE = 0;
    public static final Integer LOADED_STAGE = 1;

    /**
     * 初始化
     */
    public static ScriptStatus INIT = new ScriptStatus("初始化", INIT_STAGE);

    /**
     * 加载-未知信息
     */
    public static ScriptStatus LOADED_UNKNOWN = new ScriptStatus("已加载-未知信息", LOADED_STAGE);

    /**
     * 加载
     */
    public static ScriptStatus LOADED = new ScriptStatus("已加载", LOADED_STAGE);

}
