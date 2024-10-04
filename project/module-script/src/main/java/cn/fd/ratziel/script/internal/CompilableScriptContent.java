package cn.fd.ratziel.script.internal;

import cn.fd.ratziel.script.api.CompilableScript;
import cn.fd.ratziel.script.api.ScriptExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.script.CompiledScript;
import java.util.concurrent.CompletableFuture;

/**
 * CompilableScriptContent
 *
 * @author TheFloodDragon
 * @since 2024/10/4 20:19
 */
public class CompilableScriptContent extends BasicScriptContent implements CompilableScript {

    public CompilableScriptContent(@NotNull String content, @NotNull ScriptExecutor executor) {
        super(content, executor);
    }

    public CompletableFuture<CompiledScript> future = null;

    @Override
    public @Nullable CompiledScript getCompiled() {
        if (future != null && future.isDone())
            return future.getNow(null);
        else return null;
    }

}
