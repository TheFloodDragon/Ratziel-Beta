package cn.fd.ratziel.script;

import cn.fd.ratziel.function.argument.ArgumentContext;
import cn.fd.ratziel.function.argument.DefaultArgumentContext;
import org.jetbrains.annotations.NotNull;

import javax.script.ScriptContext;
import javax.script.SimpleScriptContext;

/**
 * SimpleScriptEnv
 *
 * @author TheFloodDragon
 * @since 2024/6/30 11:31
 */
public class SimpleScriptEnv implements ScriptEnvironment {

    public SimpleScriptEnv() {
        this(new SimpleScriptContext());
    }

    public SimpleScriptEnv(@NotNull ScriptContext scriptContext) {
        this(scriptContext, new DefaultArgumentContext());
    }

    public SimpleScriptEnv(@NotNull ScriptContext scriptContext, @NotNull ArgumentContext argumentContext) {
        this.scriptContext = scriptContext;
        this.argumentContext = argumentContext;
    }

    @NotNull
    private final ScriptContext scriptContext;
    @NotNull
    private final ArgumentContext argumentContext;

    @Override
    public @NotNull ScriptContext getScriptContext() {
        return scriptContext;
    }

    @Override
    public @NotNull ArgumentContext getArgumentContext() {
        return argumentContext;
    }

}
