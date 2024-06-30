package cn.fd.ratziel.script;

import org.jetbrains.annotations.NotNull;

import javax.script.*;

/**
 * EnginedScriptLanguage
 *
 * @author TheFloodDragon
 * @since 2024/6/30 16:39
 */
public abstract class EnginedScriptLanguage implements ScriptLanguage {

    public EnginedScriptLanguage(@NotNull String engine, @NotNull String name, String... alias) {
        this.NAME = name;
        this.ALIAS = alias;
        this.ENGINE_NAME = engine;
    }

    @NotNull
    private final String NAME;
    @NotNull
    private final String[] ALIAS;
    @NotNull
    private final String ENGINE_NAME;

    @Override
    public Object eval(@NotNull ScriptStorage script, @NotNull ScriptEnvironment environment) throws ScriptException {
        // 创建脚本引擎
        ScriptEngine engine = newEngine();
        // 获取编译后的脚本
        CompiledScript compiled = script.getCompiled();
        if (compiled == null) {
            // 没有时编译一份
            CompiledScript newCompiled = ((Compilable) engine).compile(script.getContent());
            script.setCompiled(newCompiled); // 将其设置到 ScriptStorage 中
            compiled = newCompiled;
        }
        // 评估脚本
        return compiled.eval(environment.getScriptContext());
    }

    public ScriptEngine newEngine() {
        ClassLoader classLoader = this.getClass().getClassLoader();
        ScriptEngine engine = new ScriptEngineManager(classLoader).getEngineByName(ENGINE_NAME);
        if (engine != null) return engine; else throw new NullPointerException("Cannot get ScriptEngine by name: " + ENGINE_NAME + " !");
    }

    @Override
    public @NotNull String getName() {
        return NAME;
    }

    @Override
    public @NotNull String[] getAlias() {
        return ALIAS;
    }

}
