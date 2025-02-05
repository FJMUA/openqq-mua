package cn.fjmua.mc.plugin.openqq.script;

import lombok.Data;

import javax.script.ScriptEngine;

@Data
public class ScriptParams {

    private ScriptEngine engine;

    private String content;

}
