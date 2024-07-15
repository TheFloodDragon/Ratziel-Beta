package cn.fd.ratziel.script.api;

import cn.fd.ratziel.function.argument.ArgumentContext;
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
     * 获取脚本的所有绑定
     */
    @NotNull
    Bindings getBindings();

    /**
     * 设置脚本的所有绑定
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
     * 获取脚本参数上下文
     */
    @NotNull
    ArgumentContext getContext();

    /**
     * 设置脚本参数上下文
     */
    void setContext(@NotNull ArgumentContext context);

}
