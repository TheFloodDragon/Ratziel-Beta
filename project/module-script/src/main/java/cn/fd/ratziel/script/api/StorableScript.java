package cn.fd.ratziel.script.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.script.CompiledScript;

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
     * 设置编译后的脚本内容
     */
    void setCompiled(@NotNull CompiledScript compiled);

}
