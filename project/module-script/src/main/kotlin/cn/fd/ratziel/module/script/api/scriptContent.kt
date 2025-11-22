package cn.fd.ratziel.module.script.api

/**
 * ScriptContent - 脚本内容
 *
 * @author TheFloodDragon
 * @since 2024/7/14 21:28
 */
interface ScriptContent {

    /**
     * 脚本源
     */
    val source: ScriptSource

    /**
     * 脚本内容
     */
    val content: String get() = source.content

    companion object {

        /**
         * 创建纯文本脚本内容
         */
        @JvmStatic
        fun literal(content: String, language: ScriptType) = LiteralScriptContent(ScriptSource.literal(content, language))

    }

}

/**
 * CompiledScript - 编译后的脚本
 *
 * @author TheFloodDragon
 * @since 2025/10/19 10:07
 */
interface CompiledScript : ScriptContent {

    /**
     * 评估此编译后的脚本
     */
    fun eval(environment: ScriptEnvironment): Any?

}


/**
 * LiteralScriptContent
 *
 * @author TheFloodDragon
 * @since 2024/10/4 20:11
 */
@JvmInline
value class LiteralScriptContent(override val source: LiteralScriptSource) : ScriptContent {
    override fun toString() = "LiteralScriptContent(content=${source.content})"
}

/**
 * ValuedCompiledScript
 *
 * @author TheFloodDragon
 * @since 2025/11/1 20:45
 */
abstract class ValuedCompiledScript<T>(
    /** 编译后的非封装脚本实例 **/
    val script: T,
    override val source: ScriptSource,
) : CompiledScript {
    override fun toString() = "ValuedCompiledScript(script=$script, source=$source)"
}
