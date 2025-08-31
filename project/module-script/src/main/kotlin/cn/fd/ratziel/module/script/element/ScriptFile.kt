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
    val description: Description,
) {

    /**
     * 脚本内容
     */
    val content = description.scriptFile.readText()

    /**
     * 编译后的脚本
     */
    val script: ScriptContent = executor.build(content)

    /**
     * 脚本执行器
     */
    val executor get() = description.language.executor

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