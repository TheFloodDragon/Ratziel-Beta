package cn.fd.ratziel.module.script.imports

import cn.fd.ratziel.module.script.api.ScriptType
import cn.fd.ratziel.module.script.element.ScriptElementHandler
import cn.fd.ratziel.module.script.element.ScriptFile
import taboolib.common.platform.function.warning
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
    /**
     * 脚本语言类型
     */
    val type: ScriptType,
) {

    /**
     * 脚本文件
     */
    val scriptFile: ScriptFile?
        get() {
            return ScriptElementHandler.scriptFiles[file].also {
                if (it == null) warning("No defined script file $file!")
            }
        }

    /**
     * 编译后的脚本
     */
    val compiled get() = scriptFile?.compiled

}