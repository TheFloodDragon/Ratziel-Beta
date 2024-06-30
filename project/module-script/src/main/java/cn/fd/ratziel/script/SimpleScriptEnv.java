package cn.fd.ratziel.script;

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

    public SimpleScriptEnv(@NotNull ScriptContext context) {
        this.context = context;
    }

    @NotNull
    private final ScriptContext context;

    @Override
    public @NotNull ScriptContext getScriptContext() {
        return context;
    }

}
