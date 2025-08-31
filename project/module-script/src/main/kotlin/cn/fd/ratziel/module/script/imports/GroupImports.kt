package cn.fd.ratziel.module.script.imports

import cn.fd.ratziel.module.script.ScriptType

/**
 * GroupImports - 导入组
 *
 * @author TheFloodDragon
 * @since 2025/8/11 15:06
 */
class GroupImports(
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
    fun combine(other: GroupImports) = GroupImports(
        this.classes + other.classes,
        this.packages + other.packages,
        this.scripts + other.scripts
    )

    override fun toString() = "GroupImports(classes=$classes, packages=$packages, scripts=$scripts)"

    companion object {

        /**
         * 从输出流中读取导入的包和类
         */
        @JvmStatic
        fun parse(rawContents: Iterable<String>): Pair<Set<ClassImport>, Set<PackageImport>> {
            // 过滤内容
            val contents = rawContents.map { it.trim() }
                .filterNot { it.isBlank() || it.startsWith('#') }.toSet()
            val classes = LinkedHashSet<ClassImport>()
            val packages = LinkedHashSet<PackageImport>()
            // 读取类和包
            for (import in contents) {
                if (import.endsWith('*') || import.endsWith('.')) {
                    packages.add(PackageImport(import.substringBeforeLast('.')))
                } else {
                    classes.add(ClassImport(import))
                }
            }
            return classes to packages
        }

    }

}