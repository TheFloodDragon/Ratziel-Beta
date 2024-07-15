package cn.fd.ratziel.script.impl;

import cn.fd.ratziel.function.argument.ArgumentContext;
import cn.fd.ratziel.function.argument.SimpleArgumentContext;
import cn.fd.ratziel.script.api.ScriptEnvironment;
import org.jetbrains.annotations.NotNull;

import javax.script.Bindings;
import javax.script.SimpleBindings;

/**
 * SimpleScriptEnvironment
 *
 * @author TheFloodDragon
 * @since 2024/7/15 13:41
 */
public class SimpleScriptEnvironment implements ScriptEnvironment {

    public SimpleScriptEnvironment() {
        this(new SimpleBindings());
    }

    public SimpleScriptEnvironment(@NotNull Bindings bindings) {
        this(bindings, new SimpleArgumentContext());
    }

    public SimpleScriptEnvironment(@NotNull ArgumentContext context) {
        this(new SimpleBindings(), context);
    }

    public SimpleScriptEnvironment(@NotNull Bindings bindings, @NotNull ArgumentContext context) {
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
