package cn.fd.ratziel.script;

import cn.fd.ratziel.function.argument.ArgumentContext;
import cn.fd.ratziel.function.argument.SimpleArgumentContext;
import org.jetbrains.annotations.NotNull;

import javax.script.Bindings;
import javax.script.SimpleBindings;

/**
 * SimpleScriptEnv
 *
 * @author TheFloodDragon
 * @since 2024/6/30 11:31
 */
public class SimpleScriptEnv implements ScriptEnvironment {

    public SimpleScriptEnv() {
        this(new SimpleBindings());
    }

    public SimpleScriptEnv(@NotNull Bindings bindings) {
        this(bindings, new SimpleArgumentContext());
    }

    public SimpleScriptEnv(@NotNull Bindings bindings, @NotNull ArgumentContext argumentContext) {
        this.bindings = bindings;
        this.context = argumentContext;
    }

    @NotNull
    private final Bindings bindings;
    @NotNull
    private final ArgumentContext context;

    @Override
    public @NotNull Bindings getBindings() {
        return bindings;
    }

    @Override
    public @NotNull ArgumentContext getContext() {
        return context;
    }

}
