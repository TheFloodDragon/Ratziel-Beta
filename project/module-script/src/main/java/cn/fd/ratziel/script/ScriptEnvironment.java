package cn.fd.ratziel.script;

import cn.fd.ratziel.function.argument.ArgumentContext;
import org.jetbrains.annotations.NotNull;

import javax.script.Bindings;

/**
 * ScriptEnvironment - 脚本环境
 *
 * @author TheFloodDragon
 * @since 2024/6/30 09:33
 */
public interface ScriptEnvironment {

    /**
     * 获取环境中的绑定键
     */
    @NotNull
    Bindings getBindings();

    /**
     * 获取环境中的参数上下文
     */
    @NotNull
    ArgumentContext getContext();

    /**
     * 设置绑定内容
     */
    default void set(String key, Object value) {
        getBindings().put(key, value);
    }

}