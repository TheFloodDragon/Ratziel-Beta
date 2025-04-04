package cn.fd.ratziel.module.script.impl;

import cn.fd.ratziel.module.script.api.CompilableScript;
import cn.fd.ratziel.module.script.api.ScriptExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.script.CompiledScript;
import java.util.concurrent.CompletableFuture;

/**
 * CacheableScriptContent
 * 可缓存编译后的脚本
 *
 * @author TheFloodDragon
 * @since 2024/10/4 20:19
 */
public class CacheableScriptContent extends SimpleScriptContent implements CompilableScript {

    public CacheableScriptContent(@NotNull String content, @NotNull ScriptExecutor executor) {
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
