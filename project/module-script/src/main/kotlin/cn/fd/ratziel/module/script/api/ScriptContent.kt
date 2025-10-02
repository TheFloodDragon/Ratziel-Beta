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

    /**
     * 脚本执行器
     */
    val executor: ScriptExecutor

}


/**
 * LiteralScriptContent
 *
 * @author TheFloodDragon
 * @since 2024/10/4 20:11
 */
data class LiteralScriptContent(
    override val content: String,
    override val executor: ScriptExecutor,
) : ScriptContent
