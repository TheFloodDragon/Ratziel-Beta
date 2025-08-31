package cn.fd.ratziel.module.script.imports

import cn.fd.ratziel.module.script.ScriptType

/**
 * ImportsGroup - 导入组
 *
 * @author TheFloodDragon
 * @since 2025/8/11 15:06
 */
class ImportsGroup(
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
    val scripts: Map<ScriptType, Set<ScriptImport>> = emptyMap(),
) {

    /**
     * 通过简单类名称获取已经导入的类
     * @param name 类的简单名称
     * @return [Class], 找不到则返回空
     */
    fun getImportedClass(name: String): Class<*>? {
        // 在导入的类中查找
        val find = classes.find { it.matches(name) }
        if (find != null) return find.get()
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
    fun combine(other: ImportsGroup) = ImportsGroup(
        this.classes + other.classes,
        this.packages + other.packages,
        this.scripts + other.scripts
    )

    companion object {

        // TODO
        @JvmStatic
        fun parse(rawContents: List<String>): ImportsGroup {
            val classes = LinkedHashSet<ClassImport>()
            val packages = LinkedHashSet<PackageImport>()
            for (import in rawContents) {
                if (import.endsWith('*') || import.endsWith('.')) {
                    packages.add(PackageImport(import.substringBeforeLast('.')))
                } else {
                    classes.add(ClassImport(import))
                }
            }
            return ImportsGroup(classes, packages, emptyMap())
        }

    }

}