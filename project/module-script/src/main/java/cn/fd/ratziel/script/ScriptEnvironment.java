package cn.fd.ratziel.script;

import cn.fd.ratziel.function.argument.ArgumentContext;
import org.jetbrains.annotations.NotNull;

import javax.script.Bindings;
import javax.script.ScriptContext;

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

}