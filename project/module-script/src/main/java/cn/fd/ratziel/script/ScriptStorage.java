package cn.fd.ratziel.script;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.script.CompiledScript;

/**
 * ScriptStorage - 脚本
 *
 * @author TheFloodDragon
 * @since 2024/6/30 08:14
 */
public interface ScriptStorage {

    /**
     * 获取脚本内容
     *
     * @return 脚本的字符串形式
     */
    @NotNull
    String getContent();

    /**
     * 编译过后的脚本
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