package cn.fd.ratziel.module.script.api

/**
 * ScriptContent
 *
 * @author TheFloodDragon
 * @since 2024/7/14 21:28
 */
interface ScriptContent {

    /**
     * 脚本源码内容
     */
    val content: String

    companion object {

        /**
         * 创建纯文本脚本内容
         */
        @JvmStatic
        fun literal(content: String) = LiteralScriptContent(content)

    }

}

/**
 * CompiledScript
 *
 * @author TheFloodDragon
 * @since 2025/10/19 10:07
 */
interface CompiledScript<T> : ScriptContent {

    /**
     * 编译此脚本的脚本执行器
     */
    val executor: ScriptExecutor

    /**
     * 编译后的脚本实例
     */
    val compiled: T

}


/**
 * LiteralScriptContent
 *
 * @author TheFloodDragon
 * @since 2024/10/4 20:11
 */
@JvmInline
value class LiteralScriptContent(override val content: String) : ScriptContent {
    override fun toString() = "LiteralScriptContent(content=$content)"
}
