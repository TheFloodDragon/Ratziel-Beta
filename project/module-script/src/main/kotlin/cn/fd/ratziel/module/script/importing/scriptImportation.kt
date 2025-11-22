package cn.fd.ratziel.module.script.importing

import cn.fd.ratziel.core.util.resolveBy
import cn.fd.ratziel.module.script.ScriptManager
import cn.fd.ratziel.module.script.api.ScriptType
import cn.fd.ratziel.module.script.element.ScriptElementLoader
import taboolib.common.platform.function.warning
import java.io.File

/**
 * Importation - 脚本导入件接口
 * 
 * @author TheFloodDragon
 * @since 2025/11/22 20:57
 */
interface Importation

/**
 * ImportationGroup - 脚本导入组
 *
 * @author TheFloodDragon
 * @since 2025/11/22 20:57
 */
open class ImportationGroup(
    val importations: Set<Importation> = emptySet(),
) {

    /**
     * 类导入件列表
     */
    val classes: List<ClassImportation> by lazy { importations.filterIsInstance<ClassImportation>() }

    /**
     * 包导入件列表 (指 Java 中的 package)
     */
    val packages: List<PackageImportation> by lazy { importations.filterIsInstance<PackageImportation>() }

    /**
     * 源导入件列表
     */
    val sources: Map<ScriptType, List<SourceImportation>> by lazy { importations.filterIsInstance<SourceImportation>().groupBy(SourceImportation::type) }

    /**
     * 获取指定脚本类型的指定类型的源导入件列表
     */
    fun <T : SourceImportation> getSource(type: ScriptType, klass: Class<T>): List<T> = sources[type]?.filterIsInstance(klass) ?: emptyList()

    /**
     * 获取指定脚本类型的指定类型的源导入件列表
     */
    inline fun <reified T : SourceImportation> getSource(type: ScriptType): List<T> = this.getSource(type, T::class.java)

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
     * 合并另一个导入组
     * @return 新的合并后的导入组
     */
    operator fun plus(other: ImportationGroup) = ImportationGroup(this.importations + other.importations)

    override fun toString() = "ImportationGroup(importations=$importations)"

    companion object {

        /**
         * 从文本中解析导入
         */
        @Deprecated("Need to use another approach")
        @JvmStatic
        @JvmOverloads
        fun parse(lines: Iterable<String>, baseFile: File? = null): ImportationGroup {
            // 过滤内容
            val contents = lines.map { it.trim() }
                .filterNot { it.isBlank() || it.startsWith('#') }.toSet()
            val classes = LinkedHashSet<ClassImportation>()
            val packages = LinkedHashSet<PackageImportation>()
            val scripts = LinkedHashSet<ScriptImport>()
            // 读取类和包
            for (content in contents) {
                // ~.* 格式表示包
                if (content.endsWith(".*")) {
                    packages.add(PackageImportation(content.substringBeforeLast('.')))
                } else {
                    // ~.~ 表示脚本文件
                    val type = ScriptElementLoader.matchType(content.substringAfterLast('.'))
                    if (type != null) {
                        val file = baseFile.resolveBy(content, ScriptManager.builtinScriptsResolver)
                        if (file.exists()) {
                            scripts.add(ScriptImport(content, file, type))
                        } else {
                            warning("File does not exist: $file")
                        }
                    } else {
                        // 不然就是类了 (全名)
                        classes.add(ClassImportation(content))
                    }
                }
            }
            return ImportationGroup(classes + packages + scripts)
        }

    }

}