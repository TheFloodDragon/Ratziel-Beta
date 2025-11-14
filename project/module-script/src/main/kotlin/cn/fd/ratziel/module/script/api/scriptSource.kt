package cn.fd.ratziel.module.script.api

import java.io.File

/**
 * ScriptSource - 脚本源
 *
 * @author TheFloodDragon
 * @since 2025/10/2 18:51
 */
sealed interface ScriptSource {

    /**
     * 脚本名称
     */
    val name: String? get() = null

    /**
     * 源码内容
     */
    val content: String

    /**
     * 语言类型
     */
    val language: ScriptType

    companion object {

        /**
         * 创建纯文本型脚本源
         */
        @JvmStatic
        fun literal(content: String, language: ScriptType, name: String? = null) = LiteralScriptSource(content, language, name)

        /**
         * 创建文件型脚本源
         */
        @JvmStatic
        fun filed(file: File, language: ScriptType) = FileScriptSource(file, language)

    }

}

/**
 * LiteralScriptSource
 *
 * @author TheFloodDragon
 * @since 2025/10/2 18:55
 */
data class LiteralScriptSource(
    override val content: String,
    override val language: ScriptType,
    override val name: String?,
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
    override val language: ScriptType,
) : ScriptSource {
    override val name: String get() = file.name
    override val content: String by lazy { file.readText() }
}
