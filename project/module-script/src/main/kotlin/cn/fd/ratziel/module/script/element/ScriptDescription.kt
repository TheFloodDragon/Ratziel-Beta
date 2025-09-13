package cn.fd.ratziel.module.script.element

import cn.fd.ratziel.core.util.resolveOrAbsolute
import cn.fd.ratziel.module.script.imports.GroupImports
import taboolib.common.platform.function.warning
import taboolib.module.configuration.Configuration
import java.io.File

/**
 * ScriptDescription - 脚本文件的配置描述
 *
 * @author TheFloodDragon
 * @since 2025/9/6 22:54
 */
class ScriptDescription(
    /**
     * 应用此描述的脚本文件列表
     */
    val files: List<File>,
    /**
     * 描述文件
     */
    val descFile: File? = null,
    /**
     * 脚本导入
     */
    val imports: GroupImports = GroupImports(),
) {

    override fun toString() = "{descFile=$descFile, scriptFiles=$files, imports=$imports}"

    companion object {

        /**
         * 判断文件是不是脚本描述文件
         */
        @JvmStatic
        fun isDescriptionFile(file: File): Boolean {
            // 获取配置文件类型
            val confType = Configuration.getTypeFromExtensionOrNull(file.extension) ?: return false
            // 读取配置文件
            val conf = Configuration.loadFromFile(file, confType)
            // 读取文件部分
            val filesSection = conf["file"] ?: conf["files"] ?: return false
            // 判断类型 (字符串或者列表)
            when (filesSection) {
                is String -> return true
                is List<*> -> return filesSection.isNotEmpty() && filesSection.all { it is String }
            }
            return false
        }

        /**
         * 从文件中加载 [ScriptDescription]
         */
        @JvmStatic
        fun loadFromFile(descFile: File): ScriptDescription? {
            // 读取配置文件
            val conf = Configuration.loadFromFile(descFile)

            // 解析文件路径
            val filesSection = conf["file"] ?: conf["files"] ?: return null
            val files = when (filesSection) {
                is String -> listOf(descFile.parentFile.resolveOrAbsolute(filesSection))
                is List<*> -> filesSection.mapNotNull { if (it is String) descFile.parentFile.resolveOrAbsolute(it) else null }
                else -> return null
            }.filter {
                val exists = it.exists()
                if (!exists) warning("Script file $it is not found!")
                exists // 过滤出存在的
            }

            // 解析导入项
            val importsSection = conf["imports"] ?: conf["import"]
            val imports = when (importsSection) {
                is String -> listOf(importsSection)
                is List<*> -> importsSection.map { it.toString() }
                else -> emptyList()
            }.let { GroupImports.parse(it, descFile.parentFile) }

            // 返回脚本描述
            return ScriptDescription(files, descFile, imports)
        }

    }

}