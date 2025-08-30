package cn.fd.ratziel.module.script.impl;

import cn.fd.ratziel.module.script.api.ScriptEnvironment;
import org.jetbrains.annotations.NotNull;

import javax.script.Bindings;
import javax.script.SimpleBindings;
import java.util.Map;

/**
 * ScriptEnvironmentImpl
 *
 * @author TheFloodDragon
 * @since 2025/6/6 15:14
 */
public class ScriptEnvironmentImpl implements ScriptEnvironment {

    public ScriptEnvironmentImpl() {
        this(new SimpleBindings());
    }

    public ScriptEnvironmentImpl(@NotNull final Map<String, Object> bindings) {
        this(new SimpleBindings(bindings));
    }

    public ScriptEnvironmentImpl(@NotNull final Bindings bindings) {
        this.scriptBindings = bindings;
    }

    private Bindings scriptBindings;
    private final ExecutorContext context = new ExecutorContext();

    @Override
    public @NotNull Bindings getBindings() {
        return this.scriptBindings;
    }

    @Override
    public void setBindings(final @NotNull Bindings bindings) {
        this.scriptBindings = bindings;
    }

    @Override
    public @NotNull ExecutorContext getContext() {
        return this.context;
    }

}
