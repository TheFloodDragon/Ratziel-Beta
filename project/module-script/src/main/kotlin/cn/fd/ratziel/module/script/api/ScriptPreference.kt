package cn.fd.ratziel.module.script.api

/**
 * ScriptPreference - 脚本语言偏好
 *
 * @author TheFloodDragon
 * @since 2025/10/19 01:46
 */
enum class ScriptPreference {

    /**
     * 编译需求型: 此类脚本基本只能编译后再运行
     */
    COMPILATION_REQUIRED,

    /**
     * 编译偏好型: 此类脚本可编译可解释, 一般情况下编译更优
     */
    COMPILATION_PREFERRED,

    /**
     * 解释偏好型: 此类脚本可编译可解释, 一般情况下解释更优 (解释运行也很快)
     */
    INTERPRETATION_PREFERRED;

    /**
     * 是否建议编译
     */
    val suggestingCompilation get() = this == COMPILATION_REQUIRED || this == COMPILATION_PREFERRED

}