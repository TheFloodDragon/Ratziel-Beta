package cn.fd.ratziel.script

import cn.fd.ratziel.script.api.ScriptExecutor
import cn.fd.ratziel.script.lang.JavaScriptExecutor
import cn.fd.ratziel.script.lang.KetherExecutor

/**
 * ScriptTypes - 脚本类型
 *
 * @author TheFloodDragon
 * @since 2024/7/14 21:35
 */
enum class ScriptType(
    /**
     * 执行器
     */
    val executor: ScriptExecutor?,
    /**
     * 别名
     */
    vararg val alias: String
) {

    JAVASCRIPT(null, "js", "JavaScript", "javascript", "java-script", "JS", "Js"),
    KETHER(null, "Kether", "kether", "ke", "ks"),
    JEXL(null, "Jexl", "jexl", "Jexl3", "jexl3"),
    KOTLIN_SCRIPTING(null, "Kotlin", "kotlin", "kts", "KTS");

    /**
     * 获取执行器, 若不存在则直接抛出异常
     */
    val executorOrThrow get() = executor ?: throw UnsupportedOperationException("There's no executor of $name.")

    companion object {

        /**
         * 匹配脚本类型 (无法找到时抛出异常)
         */
        @JvmStatic
        fun matchOrThrow(name: String): ScriptType =
            match(name) ?: throw IllegalArgumentException("Couldn't find ScriptLanguage with id: $name")

        /**
         * 匹配脚本类型
         */
        @JvmStatic
        fun match(name: String): ScriptType? = entries.find { it.alias.contains(name) }

    }

}