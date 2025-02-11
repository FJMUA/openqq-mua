package cn.fjmua.mc.plugin.openqq.script;

import cn.fjmua.mc.plugin.openqq.util.ParseUtil;
import lombok.Getter;
import lombok.experimental.UtilityClass;

import javax.script.*;
import java.util.concurrent.ConcurrentHashMap;

@UtilityClass
public class JavaScriptAgent {
    @Getter
    private static final ConcurrentHashMap<Script, CompiledScript> compiledScripts = new ConcurrentHashMap<>();
    private static volatile Compilable compiler;
    private static final Object LOCK = new Object();

    public static CompiledScript preCompile(Script script) {
        return compiledScripts.computeIfAbsent(script, k -> {
            try {
                return getCompiler().compile(k.getContent());
            } catch (ScriptException e) {
                throw new RuntimeException("Compilation failed for script: " + k, e);
            }
        });
    }

    /**
     * 调用脚本函数
     * @apiNote 使用默认全局上下文
     */
    public static synchronized <T> T runFunc(String methodName, Script script, Class<T> clazz) throws ScriptException, NoSuchMethodException {
        return ParseUtil.toObject(runFunc(methodName, ((ScriptEngine) getCompiler()).getContext(), script), clazz);
    }

    /**
     * 调用脚本函数
     */
    public static synchronized <T> T runFunc(String methodName, ScriptContext context, Script script, Class<T> clazz) throws ScriptException, NoSuchMethodException {
        return ParseUtil.toObject(runFunc(methodName, context, script), clazz);
    }

    /**
     * 调用脚本函数
     */
    @SuppressWarnings("unchecked")
    public static synchronized <T> T runFunc(String methodName, ScriptContext context, Script script) throws ScriptException, NoSuchMethodException {
        eval(context, script);
        Object result = ((Invocable) getCompiler()).invokeFunction(methodName);
        return (T) result;
    }

    /**
     * 加载脚本文件
     * */
    public static Object eval(ScriptContext context, Script script) {
        return eval(context, script, true);
    }

    /**
     * 加载脚本文件
     * */
    public static Object eval(ScriptContext context, Script script, boolean cacheScript) {
        synchronized (LOCK) {
            try {
                CompiledScript compiledScript = cacheScript ?
                        preCompile(script) :
                        getCompiler().compile(script.getContent());
                return compiledScript.eval(context);
            } catch (ScriptException e) {
                throw new RuntimeException("Evaluation failed for script: " + script, e);
            }
        }
    }

    private static Compilable getCompiler() {
        if (compiler == null) {
            synchronized (JavaScriptAgent.class) {
                if (compiler == null) {
                    try {
                        ScriptEngine engine = new ScriptEngineManager(JavaScriptAgent.class.getClassLoader())
                                .getEngineByName("js");
                        if (engine == null) {
                            throw new RuntimeException("JavaScript engine not found");
                        }
                        engine.getBindings(ScriptContext.ENGINE_SCOPE).put("polyglot.js.allowAllAccess", true);
                        if (!(engine instanceof Compilable)) {
                            throw new RuntimeException("The engine does not support compilation");
                        }
                        compiler = (Compilable) engine;
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to initialize compiler", e);
                    }
                }
            }
        }
        return compiler;
    }

}
