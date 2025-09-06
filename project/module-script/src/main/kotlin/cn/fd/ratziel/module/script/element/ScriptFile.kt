package cn.fd.ratziel.module.script.element

import cn.fd.ratziel.module.script.ScriptType
import cn.fd.ratziel.module.script.api.ScriptContent
import cn.fd.ratziel.module.script.api.ScriptExecutor
import java.io.File

/**
 * ScriptFile
 *
 * @author TheFloodDragon
 * @since 2025/8/31 10:20
 */
class ScriptFile(
    /** 脚本文件 **/
    val file: File,
    /** 脚本描述 **/
    val desc: ScriptDescription,
    /** 脚本语言 **/
    val language: ScriptType,
) {

    /**
     * 脚本源内容
     */
    val source = this.file.readText()

    /**
     * 脚本执行器
     */
    val executor: ScriptExecutor = this.language.executor

    /**
     * 编译后的脚本
     */
    val compiled: ScriptContent = this.executor.build(source)

    override fun toString() = "ScriptFile$desc"

}