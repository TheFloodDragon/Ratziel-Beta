package cn.fd.ratziel.module.script.api

import java.io.File

/**
 * ScriptSource
 *
 * @author TheFloodDragon
 * @since 2025/10/2 18:51
 */
interface ScriptSource {

    /**
     * 源码内容
     */
    val content: String

}

/**
 * LiteralScriptSource
 *
 * @author TheFloodDragon
 * @since 2025/10/2 18:55
 */
data class LiteralScriptSource(
    override val content: String,
) : ScriptSource

/**
 * FileScriptSource
 *
 * @author TheFloodDragon
 * @since 2025/10/2 19:00
 */
data class FileScriptSource(
    /** 脚本所在文件 **/
    val file: File,
    override val content: String = file.readText(),
) : ScriptSource
