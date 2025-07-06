package cn.fd.ratziel.module.script.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.script.Bindings;

/**
 * ScriptEnvironment - 脚本环境
 *
 * @author TheFloodDragon
 * @since 2024/7/15 13:20
 */
public interface ScriptEnvironment {

    /**
     * 获取脚本的绑定键
     */
    @NotNull
    Bindings getBindings();

    /**
     * 设置脚本的绑定键
     */
    void setBindings(@NotNull Bindings bindings);

    /**
     * 设置绑定内容
     *
     * @param key   绑定键
     * @param value 绑定内容
     */
    default void set(@NotNull String key, @Nullable Object value) {
        getBindings().put(key, value);
    }

    /**
     * 获取执行器上下文
     */
    @Nullable
    Object getExecutorContext(@NotNull ScriptExecutor executor);

    /**
     * 设置执行器上下文
     */
    void setExecutorContext(@NotNull ScriptExecutor executor, @Nullable Object context);

}
