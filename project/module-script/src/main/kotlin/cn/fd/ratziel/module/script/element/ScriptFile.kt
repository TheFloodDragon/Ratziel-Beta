package cn.fd.ratziel.module.script.element

import cn.fd.ratziel.module.script.ScriptType
import cn.fd.ratziel.module.script.api.ScriptContent
import java.io.File

/**
 * ScriptFile
 *
 * @author TheFloodDragon
 * @since 2025/8/31 10:20
 */
data class ScriptFile(
    val desc: Description,
) {

    /**
     * 脚本内容
     */
    val content = desc.scriptFile.readText()

    /**
     * 编译后的脚本
     */
    val compiled: ScriptContent = desc.language.executor.build(content)

    /**
     * Description - 脚本文件的配置描述
     *
     * @author TheFloodDragon
     * @since 2025/8/31 09:55
     */
    data class Description(
        /**
         * 脚本文件
         */
        val scriptFile: File,
        /**
         * 描述文件
         */
        val descFile: File?,
        /**
         * 脚本语言
         */
        val language: ScriptType,
    )

}