package cn.fd.ratziel.script

import cn.fd.ratziel.script.api.ScriptEnvironment
import cn.fd.ratziel.script.api.ScriptExecutor
import cn.fd.ratziel.script.api.ScriptType
import cn.fd.ratziel.script.jexl.JexlExecutor
import cn.fd.ratziel.script.js.JavaScriptExecutor
import cn.fd.ratziel.script.kether.KetherExecutor
import cn.fd.ratziel.script.kts.KotlinScriptExecutor
import java.util.concurrent.CopyOnWriteArrayList

/**
 * ScriptTypes
 *
 * @author TheFloodDragon
 * @since 2024/7/14 21:35
 */
enum class ScriptTypes(
    /**
     * 执行器
     */
    private val executor: ScriptExecutor,
    /**
     * 名称
     */
    private vararg val names: String,
    /**
     * 是否启用
     */
    var enabled: Boolean = true,
    /**
     * [ScriptEnvironment.Applier]
     */
    val appliers: MutableList<ScriptEnvironment.Applier> = CopyOnWriteArrayList()
) : ScriptType {

    JAVASCRIPT(JavaScriptExecutor, "js", "JavaScript", "javascript", "java-script", "JS", "Js"),
    KETHER(KetherExecutor, "Kether", "kether", "ke", "ks"),
    JEXL(JexlExecutor, "Jexl", "jexl", "Jexl3", "jexl3"),
    KOTLIN_SCRIPTING(KotlinScriptExecutor, "Kotlin", "kotlin", "kts", "KTS");

    override fun getExecutor() = if (enabled) executor else throw IllegalStateException("Script $name is disabled!")

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