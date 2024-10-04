package cn.fd.ratziel.script;

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
