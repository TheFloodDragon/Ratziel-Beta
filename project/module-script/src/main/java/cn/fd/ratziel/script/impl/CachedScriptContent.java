package cn.fd.ratziel.script.impl;

import cn.fd.ratziel.script.api.CompilableScript;
import cn.fd.ratziel.script.api.ScriptExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.script.CompiledScript;
import java.util.concurrent.CompletableFuture;

/**
 * CachedScriptContent
 *
 * @author TheFloodDragon
 * @since 2024/10/4 20:19
 */
public class CachedScriptContent extends SimpleScriptContent implements CompilableScript {

    public CachedScriptContent(@NotNull String content, @NotNull ScriptExecutor executor) {
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
