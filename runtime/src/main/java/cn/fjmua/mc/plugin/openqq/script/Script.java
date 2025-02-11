package cn.fjmua.mc.plugin.openqq.script;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.io.File;

@Data
public class Script implements Comparable<Script> {

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

    // 脚本文件实例
    private File file;

    // 脚本内容
    private String content;

    // 脚本状态
    private Status status = Status.INIT;

    @Override
    public int compareTo(Script script) {
        return priority.compareTo(script.priority);
    }

    /**
     * 脚本状态枚举
     */
    @Getter
    @AllArgsConstructor
    public enum Status {

        /**
         * 初始化
         */
        INIT("初始化"),

        /**
         * 加载-未知信息
         */
        LOADED_UNKNOWN("已加载-未知信息"),

        /**
         * 加载
         */
        LOADED("已加载");

        private final String description;

    }

}
