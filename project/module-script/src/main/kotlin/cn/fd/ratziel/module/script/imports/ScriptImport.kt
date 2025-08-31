package cn.fd.ratziel.module.script.imports

import cn.fd.ratziel.module.script.ScriptType
import cn.fd.ratziel.module.script.element.ScriptElementManager
import cn.fd.ratziel.module.script.element.ScriptFile
import java.io.File

/**
 * ScriptImport
 *
 * @author TheFloodDragon
 * @since 2025/8/31 10:52
 */
data class ScriptImport(
    /**
     * 脚本文件路径
     */
    val file: File,
) {

    /**
     * 脚本
     */
    val script: ScriptFile
        get() {
            return ScriptElementManager.scriptFiles[file]
                ?: throw NoSuchElementException("Cannot find script by $file!")
        }

    /**
     * 脚本语言类型
     */
    val type: ScriptType get() = script.desc.language

}