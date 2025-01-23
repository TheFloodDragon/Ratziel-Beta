package cn.fd.ratziel.script;

import cn.fd.ratziel.script.api.ScriptEnvironment;
import org.jetbrains.annotations.NotNull;

import javax.script.ScriptContext;
import javax.script.SimpleScriptContext;

/**
 * SimpleScriptEnv
 *
 * @author TheFloodDragon
 * @since 2024/7/15 13:41
 */
public class SimpleScriptEnv implements ScriptEnvironment {

    public SimpleScriptEnv() {
        this(new SimpleScriptContext());
    }

    public SimpleScriptEnv(@NotNull ScriptContext context) {
        this.context = context;
    }

    private final ScriptContext context;

    @Override
    public @NotNull ScriptContext getContext() {
        return context;
    }

}
