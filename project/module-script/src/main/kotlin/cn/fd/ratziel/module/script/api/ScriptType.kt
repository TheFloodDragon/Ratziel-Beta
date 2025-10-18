package cn.fd.ratziel.module.script.api

import cn.fd.ratziel.module.script.lang.jexl.JexlLang
import cn.fd.ratziel.module.script.lang.js.JavaScriptLang
import cn.fd.ratziel.module.script.lang.kts.KotlinScriptingLang
import java.util.concurrent.CopyOnWriteArraySet

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
    val enabled get() = this in enabledLanguages

    companion object {

        /**
         * 脚本类型注册表
         */
        @JvmStatic
        val registry: MutableSet<ScriptType> = CopyOnWriteArraySet()

        /**
         * 启用的脚本语言列表
         */
        internal var enabledLanguages: MutableSet<ScriptType> = CopyOnWriteArraySet()

        /** JavaScript **/
        @JvmStatic
        val JAVASCRIPT = register(JavaScriptLang)

        /** Jexl **/
        @JvmStatic
        val JEXL = register(JexlLang)

        /** Kotlin Scripting **/
        @JvmStatic
        val KOTLIN_SCRIPTING = register(KotlinScriptingLang)

        /**
         * 匹配脚本类型
         */
        @JvmStatic
        @JvmOverloads
        fun match(name: String, onlyActive: Boolean = true): ScriptType? {
            val cleaned = name.filterNot { it.isWhitespace() }
            return (if (onlyActive) enabledLanguages else registry)
                .find { type ->
                    type.name.equals(cleaned, true) || type.alias.any { it.equals(cleaned, true) }
                }
        }

        /**
         * 匹配脚本类型 (无法找到时抛出异常)
         */
        @JvmStatic
        fun matchOrThrow(name: String) = match(name) ?: throw IllegalArgumentException("Couldn't find script-language by id: $name")


        private fun <T : ScriptType> register(scriptType: T): T {
            this.registry.add(scriptType)
            return scriptType
        }

    }

}