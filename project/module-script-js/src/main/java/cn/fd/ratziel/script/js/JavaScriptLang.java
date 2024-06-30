package cn.fd.ratziel.script.js;

import cn.fd.ratziel.script.ScriptEnvironment;
import cn.fd.ratziel.script.ScriptLanguage;
import cn.fd.ratziel.script.ScriptStorage;
import org.jetbrains.annotations.NotNull;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * JavaScriptLang
 *
 * @author TheFloodDragon
 * @since 2024/6/30 08:54
 */
public final class JavaScriptLang implements ScriptLanguage {

    private static final String name = "js";

    private static final String[] alias = new String[]{"JavaScript", "javascript", "java-script"};

    private static final JavaScriptLang INSTANCE = new JavaScriptLang();

    public static @NotNull JavaScriptLang getInstance() {
        return INSTANCE;
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public @NotNull String[] getAlias() {
        return alias;
    }

    @Override
    public Object eval(@NotNull ScriptStorage script, @NotNull ScriptEnvironment environment) throws ScriptException {
        // 创建脚本引擎
        ScriptEngine engine = newEngine();
        // 获取编译后的脚本
        CompiledScript compiled = script.getCompiled();
        if (compiled == null) {
            script.setCompiled(((Compilable) engine).compile(script.getContent()));
            compiled = script.getCompiled();
        }
        // 评估脚本
        return compiled.eval(environment.getScriptContext());
    }

    public @NotNull ScriptEngine newEngine() {
        return new ScriptEngineManager().getEngineByName("nashorn");
    }

}