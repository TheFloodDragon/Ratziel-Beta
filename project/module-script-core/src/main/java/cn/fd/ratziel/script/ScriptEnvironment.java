package cn.fd.ratziel.script;

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
     * 获取环境中的脚本上下文
     *
     * @return 脚本上下文, 不能为空
     */
    @NotNull
    ScriptContext getScriptContext();

    /**
     * 获取环境中脚本上下文的绑定键 (在引擎域中)
     */
    @NotNull
    default Bindings getScriptBindings() {
        return getScriptContext().getBindings(ScriptContext.ENGINE_SCOPE);
    }

}