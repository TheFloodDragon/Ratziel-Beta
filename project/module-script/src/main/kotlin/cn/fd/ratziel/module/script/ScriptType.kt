package cn.fd.ratziel.module.script

import cn.fd.ratziel.module.script.api.ScriptExecutor
import cn.fd.ratziel.module.script.lang.JavaScriptExecutor
import java.util.concurrent.CopyOnWriteArraySet

/**
 * ScriptTypes - 脚本类型
 *
 * @author TheFloodDragon
 * @since 2024/7/14 21:35
 */
interface ScriptType {

    /**
     * 是否启用脚本
     */
    var enabled: Boolean

    /**
     * 执行器
     */
    var executor: ScriptExecutor?

    /**
     * 别名
     */
    val alias: Array<out String>

    /**
     * 获取执行器, 若不存在则直接抛出异常
     */
    val executorOrThrow get() = executor ?: throw UnsupportedOperationException("There's no executor of $this.")

    companion object {

        /**
         * 脚本类型注册表
         */
        @JvmStatic
        val registry: MutableSet<ScriptType> = CopyOnWriteArraySet()

        /** JavaScript **/
        @JvmStatic
        val JAVASCRIPT = register(JavaScriptExecutor, "js", "javascript")

        /** Kether **/
        @JvmStatic
        val KETHER = register(null, "kether", "ke", "ks")

        /** Jexl **/
        @JvmStatic
        val JEXL = register(null, "jexl", "jexl3")

        /** Kotlin Scripting **/
        @JvmStatic
        val KOTLIN_SCRIPTING = register(null, "kotlin", "kts")

        /**
         * 匹配脚本类型
         */
        @JvmStatic
        fun match(name: String): ScriptType? = registry.find { it.enabled && it.alias.contains(name.lowercase()) }

        /**
         * 匹配脚本类型 (无法找到时抛出异常)
         */
        @JvmStatic
        fun matchOrThrow(name: String): ScriptType =
            match(name) ?: throw IllegalArgumentException("Couldn't find script-language by id: $name")

        internal fun register(executor: ScriptExecutor?, vararg alias: String) =
            object : ScriptType {
                override var enabled = false
                override var executor = executor
                override val alias = alias
                override fun toString() = "ScriptType(enabled=${this.enabled}, executor=${this.executor}, alias=${this.alias.contentToString()})"
            }.also { this.registry.add(it) }

    }

}