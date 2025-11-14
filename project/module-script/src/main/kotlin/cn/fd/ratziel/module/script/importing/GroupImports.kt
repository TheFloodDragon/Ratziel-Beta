package cn.fd.ratziel.module.script.importing

import cn.fd.ratziel.core.contextual.AttachedContext
import cn.fd.ratziel.core.util.resolveBy
import cn.fd.ratziel.module.script.ScriptManager
import cn.fd.ratziel.module.script.api.ScriptType
import cn.fd.ratziel.module.script.element.ScriptElementLoader
import taboolib.common.platform.function.warning
import java.io.File

/**
 * GroupImports - 导入组
 *
 * @author TheFloodDragon
 * @since 2025/8/11 15:06
 */
open class GroupImports(
    /**
     * 导入的类
     */
    val classes: Set<ClassImport> = emptySet(),
    /**
     * 导入的包
     */
    val packages: Set<PackageImport> = emptySet(),
    /**
     * 导入的脚本
     */
    val scripts: Set<ScriptImport> = emptySet(),
) {

    /**
     * 通过简单类名称获取已经导入的类
     * @param name 类的简单名称
     * @return [Class], 找不到则返回空
     */
    fun lookupClass(name: String): Class<*>? {
        // 在导入的类中查找
        val find = classes.find { it.simpleName == name }?.get()
        if (find != null) return find
        // 在导入的包中查找
        for (import in packages) {
            val searched = import.search(name)
            if (searched != null) return searched
        }
        return null
    }

    /**
     * 获取指定类型的脚本导入列表
     */
    fun scripts(type: ScriptType): List<ScriptImport> = this.scripts.filter { it.type == type }

    /**
     * 合并另一个导入组
     * @return 新的合并后的导入组
     */
    operator fun plus(other: GroupImports) = GroupImports(
        this.classes + other.classes,
        this.packages + other.packages,
        this.scripts + other.scripts
    )

    override fun toString() = "GroupImports(classes=$classes, packages=$packages, scripts=$scripts)"

    /**
     * 默认脚本导入
     */
    object Default : GroupImports()

    companion object {

        /**
         * 从环境中获取 [GroupImports]
         */
        @JvmField
        val catcher = AttachedContext.catcher(this) { ScriptManager.globalGroup }

        /**
         * 从文本中解析导入
         */
        @JvmStatic
        @JvmOverloads
        fun parse(lines: Iterable<String>, baseFile: File? = null): GroupImports {
            // 过滤内容
            val contents = lines.map { it.trim() }
                .filterNot { it.isBlank() || it.startsWith('#') }.toSet()
            val classes = LinkedHashSet<ClassImport>()
            val packages = LinkedHashSet<PackageImport>()
            val scripts = LinkedHashSet<ScriptImport>()
            // 读取类和包
            for (content in contents) {
                // ~.* 格式表示包
                if (content.endsWith(".*")) {
                    packages.add(PackageImport(content.substringBeforeLast('.')))
                } else {
                    // ~.~ 表示脚本文件
                    val type = ScriptElementLoader.matchType(content.substringAfterLast('.'))
                    if (type != null) {
                        val file = baseFile.resolveBy(content, ScriptManager.builtinScriptsResolver)
                        if (file.exists()) {
                            scripts.add(ScriptImport(file, type))
                        } else {
                            warning("File does not exist: $file")
                        }
                    } else {
                        // 不然就是类了 (全名)
                        classes.add(ClassImport(content))
                    }
                }
            }
            return GroupImports(classes, packages, scripts)
        }

    }

}