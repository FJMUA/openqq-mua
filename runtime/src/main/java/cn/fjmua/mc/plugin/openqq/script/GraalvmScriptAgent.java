package cn.fjmua.mc.plugin.openqq.script;

import cn.fjmua.mc.plugin.openqq.exception.ScriptMemberNotFoundException;
import cn.fjmua.mc.plugin.openqq.util.ParseUtil;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @apiNote 本类中所有方法均应被设计为线程安全
 * */
@SuppressWarnings("unused")
@UtilityClass
public class GraalvmScriptAgent {

    public static final String LANGUAGE_ID = "js";

    @Getter
    private static final ConcurrentHashMap<Script, Context> scriptContextMap = new ConcurrentHashMap<>();

    @NotNull
    public static <T> T runFunc(String funcName, Script script, Class<T> clazz, Object... args) throws ScriptMemberNotFoundException {
        Value field = getField(funcName, script);
        if (field == null) {
            throw new ScriptMemberNotFoundException(funcName);
        }
        return ParseUtil.toObject(field.execute(args).as(Map.class), clazz);
    }

    @Nullable
    public static Value runFunc(String funcName, Script script, Object... args) throws ScriptMemberNotFoundException {
        Value field = getField(funcName, script);
        if (field == null) {
            throw new ScriptMemberNotFoundException(funcName);
        }
        return field.execute(args);
    }

    @NotNull
    public static <T> T getField(String fieldName, Script script, Class<T> clazz) throws ScriptMemberNotFoundException {
        Value member = getBindings(script).getMember(fieldName);
        if (member == null) {
            throw new ScriptMemberNotFoundException(fieldName);
        }
        return ParseUtil.toObject(member.as(Map.class), clazz);
    }

    @Nullable
    public static Value getField(String fieldName, Script script)  {
        return getBindings(script).getMember(fieldName);
    }

    @NotNull
    public static Context getContext(Script script) {
        return getContext(script, false);
    }

    @NotNull
    public static Context getContext(Script script, boolean cache) {
        return cache ? scriptContextMap.computeIfAbsent(script, k -> newContext(script)) : newContext(script);
    }

    @NotNull
    private static Context newContext(Script script) {
        Context context = Context.newBuilder()
                .allowAllAccess(true)
                .hostClassLoader(GraalvmScriptAgent.class.getClassLoader())
                .build();
        Value bindings = context.getBindings(LANGUAGE_ID);
        for (Map.Entry<String, Object> entry : script.getBindingsMap().entrySet()) {
            bindings.putMember(entry.getKey(), entry.getValue());
        }
        context.eval(LANGUAGE_ID, script.getContent());
        return context;
    }

    @NotNull
    private static Value getBindings(Script script) {
        Context context = getContext(script);
        return context.getBindings(LANGUAGE_ID);
    }

}
