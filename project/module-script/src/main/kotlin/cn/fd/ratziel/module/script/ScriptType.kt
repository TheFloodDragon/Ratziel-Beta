package cn.fd.ratziel.module.script

import cn.fd.ratziel.module.script.api.ScriptExecutor
import cn.fd.ratziel.module.script.lang.JavaScriptExecutor
import cn.fd.ratziel.module.script.lang.JexlScriptExecutor
import cn.fd.ratziel.module.script.lang.KetherExecutor
import java.util.concurrent.CopyOnWriteArraySet
import java.util.function.Supplier

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
     * 是否启用脚本
     */
    var enabled: Boolean

    /**
     * 执行器
     */
    var executor: Supplier<ScriptExecutor?>

    /**
     * 别名
     */
    val alias: Array<out String>

    /**
     * 获取执行器, 若不存在则直接抛出异常
     */
    val executorOrThrow get() = executor.get() ?: throw UnsupportedOperationException("There's no executor of language '$name'.")

    companion object {

        /**
         * 脚本类型注册表
         */
        @JvmStatic
        val registry: MutableSet<ScriptType> = CopyOnWriteArraySet()

        /** JavaScript **/
        @JvmStatic
        val JAVASCRIPT = register("JavaScript", "Js") { JavaScriptExecutor }

        /** Kether **/
        @Suppress("unused")
        @JvmStatic
        val KETHER = register("Kether", "ke", "ks") { KetherExecutor }

        /** Jexl **/
        @Suppress("unused")
        @JvmStatic
        val JEXL = register("Jexl", "Jexl3") { JexlScriptExecutor }

        /** Kotlin Scripting **/
        @Suppress("unused")
        @JvmStatic
        val KOTLIN_SCRIPTING = register("KotlinScripting", "Kotlin", "kts") { null }

        /**
         * 匹配脚本类型
         */
        @JvmStatic
        fun match(name: String): ScriptType? = registry.find { s ->
            s.name.equals(name, true) || s.alias.any { it.equals(name, true) }
        }

        /**
         * 匹配脚本类型 (无法找到时抛出异常)
         */
        @JvmStatic
        fun matchOrThrow(name: String): ScriptType =
            match(name) ?: throw IllegalArgumentException("Couldn't find script-language by id: $name")

        private fun register(name: String, vararg alias: String, executor: Supplier<ScriptExecutor?>) =
            object : ScriptType {
                override val name = name
                override var enabled = false
                override var executor = executor
                override val alias = alias
                override fun toString() = "ScriptType(name=$name, enabled=$enabled, alias=${this.alias.contentToString()})"
            }.also { this.registry.add(it) }

    }

}