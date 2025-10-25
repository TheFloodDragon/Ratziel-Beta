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
interface CompiledScript : ScriptContent {

    /**
     * 编译此脚本的脚本执行器
     */
    val executor: ScriptExecutor

    /**
     * 评估此编译后的脚本
     */
    fun evaluate(environment: ScriptEnvironment): Result<Any?>

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
