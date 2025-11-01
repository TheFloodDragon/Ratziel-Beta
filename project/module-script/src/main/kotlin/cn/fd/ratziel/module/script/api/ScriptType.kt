package cn.fd.ratziel.module.script.api

import cn.fd.ratziel.module.script.ScriptService

/**
 * ScriptTypes - 脚本类型
 *
 * @author TheFloodDragon
 * @since 2024/7/14 21:35
 */
interface ScriptType {

    /**
     * 脚本名称
     */
    val name: String

    /**
     * 语言ID
     */
    val languageId: String

    /**
     * 别名
     */
    val alias: Array<out String>

    /**
     * 文件扩展名
     */
    val extensions: Array<out String> get() = emptyArray()

    /**
     * 脚本偏好
     */
    val preference: ScriptPreference

    /**
     * 获取脚本执行器
     */
    val executor: ScriptExecutor
        get() = throw UnsupportedOperationException("There's no executor of language '$name'.")

    /**
     * 脚本是否启用
     */
    val isEnabled get() = ScriptService.isEnabled(this)

    companion object {

        /**
         * 匹配脚本类型 (无法找到时抛出异常)
         */
        @JvmStatic
        @JvmOverloads
        fun matchOrThrow(name: String, onlyEnabled: Boolean = true) = ScriptService.matchOrThrow(name, onlyEnabled)

        /**
         * 匹配脚本类型
         */
        @JvmStatic
        @JvmOverloads
        fun match(name: String, onlyEnabled: Boolean = true): ScriptType? = runCatching { this.matchOrThrow(name, onlyEnabled) }.getOrNull()

    }

}