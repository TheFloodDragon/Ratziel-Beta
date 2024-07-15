package cn.fd.ratziel.script.impl;

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
        this.bindings = bindings;
    }

    private Bindings bindings;

    @Override
    public @NotNull Bindings getBindings() {
        return bindings;
    }

    @Override
    public void setBindings(@NotNull Bindings bindings) {
        this.bindings = bindings;
    }

}
