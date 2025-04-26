package cn.fd.ratziel.module.script.impl;

import cn.fd.ratziel.module.script.api.ScriptExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

/**
 * CompletableScript
 *
 * @author TheFloodDragon
 * @since 2024/10/4 20:19
 */
public class CompletableScript<T> extends SimpleScriptContent {

    public CompletableScript(@NotNull String content, @NotNull ScriptExecutor executor) {
        super(content, executor);
    }

    private final CompletableFuture<T> future = new CompletableFuture<>();

    /**
     * 完成编译
     *
     * @param obj 传入的脚本对象
     */
    public void complete(@NotNull T obj) {
        future.complete(obj);
    }

    /**
     * 获取编译完成的脚本对象
     *
     * @return 若未完成则返回为空
     */
    public @Nullable T getCompleted() {
        if (future.isDone())
            return future.getNow(null);
        else return null;
    }

}
