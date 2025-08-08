package cn.fd.ratziel.module.script.impl;

import cn.fd.ratziel.core.functional.AttachedContext;
import cn.fd.ratziel.module.script.api.ScriptEnvironment;
import cn.fd.ratziel.module.script.api.ScriptExecutor;
import org.jetbrains.annotations.NotNull;

import javax.script.Bindings;
import javax.script.SimpleBindings;
import java.util.HashMap;
import java.util.Map;

/**
 * SimpleScriptEnvironment
 *
 * @author TheFloodDragon
 * @since 2025/6/6 15:14
 */
public class SimpleScriptEnvironment implements ScriptEnvironment {

    public SimpleScriptEnvironment() {
        this(new SimpleBindings());
    }

    public SimpleScriptEnvironment(@NotNull final Bindings scriptBindings) {
        this.scriptBindings = scriptBindings;
    }

    private Bindings scriptBindings;
    private final Map<ScriptExecutor, Object> executorContexts = new HashMap<>();
    private final AttachedContext attachedContext = new AttachedContext();

    @Override
    public @NotNull Bindings getBindings() {
        return this.scriptBindings;
    }

    @Override
    public void setBindings(final @NotNull Bindings bindings) {
        this.scriptBindings = bindings;
    }

    @Override
    public @NotNull AttachedContext getAttachedContext() {
        return this.attachedContext;
    }

}
