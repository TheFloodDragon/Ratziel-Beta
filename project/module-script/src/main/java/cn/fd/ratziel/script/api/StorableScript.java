package cn.fd.ratziel.script.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.script.CompiledScript;
import javax.script.ScriptException;

/**
 * StorableScript
 *
 * @author TheFloodDragon
 * @since 2024/7/15 13:18
 */
public interface StorableScript {

    /**
     * 获取编译过后的脚本
     *
     * @return 如果此脚本不能被编译, 则返回空
     */
    @Nullable
    CompiledScript getCompiled();

    /**
     * 编译脚本
     *
     * @param executor 脚本执行器
     * @return 脚本是否可编译
     * @throws ScriptException 编译过程中出现异常时抛出
     */
    default boolean compile(@NotNull ScriptExecutor executor) throws ScriptException {
        return false;
    }

}
