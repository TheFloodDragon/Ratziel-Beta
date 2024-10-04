package cn.fd.ratziel.script.api;

import org.jetbrains.annotations.Nullable;

import javax.script.CompiledScript;

/**
 * CompilableScript
 *
 * @author TheFloodDragon
 * @since 2024/7/15 13:18
 */
public interface CompilableScript {

    /**
     * 获取编译过后的脚本
     *
     * @return 如果此脚本不能被编译, 则返回空
     */
    @Nullable
    CompiledScript getCompiled();

}
