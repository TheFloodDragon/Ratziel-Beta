package cn.fd.ratziel.script;

import cn.fd.ratziel.function.ArgumentContext;
import cn.fd.ratziel.function.SimpleArgumentContext;
import cn.fd.ratziel.script.api.ScriptEnvironment;
import org.jetbrains.annotations.NotNull;

import javax.script.Bindings;
import javax.script.SimpleBindings;

/**
 * SimpleScriptEnv
 *
 * @author TheFloodDragon
 * @since 2024/7/15 13:41
 */
public class SimpleScriptEnv implements ScriptEnvironment {

    public SimpleScriptEnv() {
        this(new SimpleBindings());
    }

    public SimpleScriptEnv(@NotNull Bindings bindings) {
        this(bindings, new SimpleArgumentContext());
    }

    public SimpleScriptEnv(@NotNull ArgumentContext context) {
        this(new SimpleBindings(), context);
    }

    public SimpleScriptEnv(@NotNull Bindings bindings, @NotNull ArgumentContext context) {
        this.bindings = bindings;
        this.context = context;
    }

    private Bindings bindings;
    private ArgumentContext context;

    @Override
    public @NotNull Bindings getBindings() {
        return bindings;
    }

    @Override
    public void setBindings(@NotNull Bindings bindings) {
        this.bindings = bindings;
    }

    @Override
    public @NotNull ArgumentContext getContext() {
        return context;
    }

    @Override
    public void setContext(@NotNull ArgumentContext context) {
        this.context = context;
    }

}
