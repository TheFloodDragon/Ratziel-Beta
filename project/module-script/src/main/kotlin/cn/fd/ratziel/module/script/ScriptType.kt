package cn.fd.ratziel.module.script

import cn.fd.ratziel.module.script.api.ScriptExecutor
import java.util.*
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
     * 获取脚本执行器
     */
    val executor: ScriptExecutor
        get() = throw UnsupportedOperationException("There's no executor of language '$name'.")

    companion object {

        /**
         * 脚本类型注册表
         */
        val registry: MutableSet<ScriptType> = CopyOnWriteArraySet()

        /**
         * 启用的脚本语言列表
         */
        var activeLanguages: Set<ScriptType> = registry
            internal set

        init {
            // SPI 注册
            val languages = ServiceLoader.load(ScriptType::class.java)
            for (lang in languages) this.registry.add(lang)
        }

        /**
         * 匹配脚本类型
         */
        @JvmStatic
        fun match(name: String): ScriptType? {
            val trimmed = name.trim()
            return activeLanguages.find { type ->
                type.name.equals(trimmed, true) || type.alias.any { it.equals(trimmed, true) }
            }
        }

        /**
         * 匹配脚本类型 (无法找到时抛出异常)
         */
        @JvmStatic
        fun matchOrThrow(name: String) = match(name) ?: throw IllegalArgumentException("Couldn't find script-language by id: $name")

    }

}