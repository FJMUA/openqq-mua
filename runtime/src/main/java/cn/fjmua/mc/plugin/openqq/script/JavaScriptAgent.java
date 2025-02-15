//package cn.fjmua.mc.plugin.openqq.script;
//
//import cn.fjmua.mc.plugin.openqq.util.ParseUtil;
//import lombok.Getter;
//import lombok.experimental.UtilityClass;
//
//import javax.script.*;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
//@SuppressWarnings("unused")
//@UtilityClass
//public class JavaScriptAgent {
//    @Getter
//    private static final ConcurrentHashMap<Script, CompiledScript> compiledScripts = new ConcurrentHashMap<>();
//    private static volatile Compilable compiler;
//    // 确保每个线程的上下文唯一
//    private static final ThreadLocal<ScriptContext> threadCurrentContext = new ThreadLocal<>();
//    private static final Object LOCK = new Object();
//
//    public static CompiledScript preCompile(Script script) {
//        return compiledScripts.computeIfAbsent(script, k -> {
//            try {
//                return getCompiler().compile(k.getContent());
//            } catch (ScriptException e) {
//                throw new RuntimeException("Compilation failed for script: " + k, e);
//            }
//        });
//    }
//
//    /**
//     * 调用脚本函数
//     * @apiNote 使用默认的脚本隔离上下文
//     */
//    public static <T> T runFunc(String methodName, Script script, Class<T> clazz) throws ScriptException, NoSuchMethodException {
//        return ParseUtil.toObject(runFunc(methodName, script), clazz);
//    }
//
//    /**
//     * 调用脚本函数
//     */
//    @SuppressWarnings("unchecked")
//    public static <T> T runFunc(String methodName, Script script) throws ScriptException, NoSuchMethodException {
//        setContext(script);
//        Object result = ((Invocable) getCompiler()).invokeFunction(methodName);
//        return (T) result;
//    }
//
//    /**
//     * 获取脚本变量
//     * @apiNote 使用默认的脚本隔离上下文
//     */
//    @Deprecated
//    public static <T> T getField(String methodName, Script script, Class<T> clazz) {
//        return ParseUtil.toObject(getField(methodName, script), clazz);
//    }
//
//    /**
//     * 获取脚本变量
//     */
//    @Deprecated
//    @SuppressWarnings("unchecked")
//    public static <T> T getField(String fieldName, Script script) {
//        setContext(script);
//        Object result = ((ScriptEngine) getCompiler()).get(fieldName);
//        return (T) result;
//    }
//
//    /**
//     * 设置脚本变量
//     */
//    public static void setBindings(Map<String, Object> bindings, Script script) {
//        setContext(script, bindings);
//    }
//
//    /**
//     * 加载脚本文件
//     * */
//    public static Object eval(Script script) {
//        return eval(script, true);
//    }
//
//    /**
//     * 加载脚本文件
//     * */
//    public static Object eval(Script script, boolean cacheScript) {
//        synchronized (LOCK) {
//            try {
//                CompiledScript compiledScript = cacheScript ?
//                        preCompile(script) :
//                        getCompiler().compile(script.getContent());
//                return compiledScript.eval(script.getContext());
//            } catch (ScriptException e) {
//                throw new RuntimeException("Evaluation failed for script: " + script, e);
//            }
//        }
//    }
//
//    private static void setContext(Script script) {
//        setContext(script, null);
//    }
//
//    private static void setContext(Script script, Map<String, Object> bindingsMap) {
//        ScriptContext context = script.getContext();
//        if (context.equals(threadCurrentContext.get())) {
//            return;
//        }
//        ScriptEngine engine = (ScriptEngine) getCompiler();
//        engine.setContext(context);
//        if (bindingsMap != null) {
//            Bindings bindings = engine.getBindings(ScriptContext.GLOBAL_SCOPE);
//            if (bindings == null) {
//                bindings = new SimpleBindings();
//            }
//            bindings.putAll(bindingsMap);
//            engine.setBindings(bindings, ScriptContext.GLOBAL_SCOPE);
//        }
//        eval(script);
//        threadCurrentContext.set(context);
//    }
//
//    private static Compilable getCompiler() {
//        if (compiler == null) {
//            synchronized (JavaScriptAgent.class) {
//                if (compiler == null) {
//                    try {
//                        ScriptEngine engine = new ScriptEngineManager(JavaScriptAgent.class.getClassLoader())
//                                .getEngineByName("js");
//                        if (engine == null) {
//                            throw new RuntimeException("JavaScript engine not found");
//                        }
//                        engine.getBindings(ScriptContext.ENGINE_SCOPE).put("polyglot.js.allowAllAccess", true);
//                        if (!(engine instanceof Compilable)) {
//                            throw new RuntimeException("The engine does not support compilation");
//                        }
//                        compiler = (Compilable) engine;
//                    } catch (Exception e) {
//                        throw new RuntimeException("Failed to initialize compiler", e);
//                    }
//                }
//            }
//        }
//        return compiler;
//    }
//
//}
