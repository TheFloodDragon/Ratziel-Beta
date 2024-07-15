package cn.fd.ratziel.script

import cn.fd.ratziel.script.api.ScriptExecutor
import cn.fd.ratziel.script.api.ScriptType
import cn.fd.ratziel.script.executors.JavaScriptExecutor
import cn.fd.ratziel.script.executors.JexlExecutor
import cn.fd.ratziel.script.executors.KetherExecutor
import cn.fd.ratziel.script.executors.KotlinScriptExecutor

/**
 * ScriptTypes
 *
 * @author TheFloodDragon
 * @since 2024/7/14 21:35
 */
enum class ScriptTypes(private val executor: ScriptExecutor, private vararg val names: String) : ScriptType {

    JAVASCRIPT(JavaScriptExecutor, "js", "JavaScript", "javascript", "java-script", "JS"),
    KETHER(KetherExecutor, "Kether", "kether", "ke", "ks"),
    JEXL(JexlExecutor, "Jexl", "jexl", "Jexl3", "jexl3"),
    KOTLIN_SCRIPTING(KotlinScriptExecutor, "Kotlin", "kotlin", "kts", "KTS");

    override fun getExecutor() = executor

    override fun getNames() = names

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
        fun match(name: String): ScriptType? = entries.find { it.names.contains(name) }

    }

}