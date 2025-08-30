package cn.fd.ratziel.module.script.imports

/**
 * ImportsGroup - 导入组
 *
 * @author TheFloodDragon
 * @since 2025/8/11 15:06
 */
class ImportsGroup(
    /**
     * 原始导入的内容
     */
    val rawContents: List<String>,
    /**
     * 导入的类
     */
    val classes: List<ClassImport>,
    /**
     * 导入的包
     */
    val packages: List<PackageImport>,
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

    companion object {

        @JvmStatic
        fun parse(rawContents: List<String>): ImportsGroup {
            val classes = ArrayList<ClassImport>()
            val packages = ArrayList<PackageImport>()
            for (import in rawContents) {
                if (import.endsWith('*') || import.endsWith('.')) {
                    packages.add(PackageImport(import.substringBeforeLast('.')))
                } else {
                    classes.add(ClassImport(import))
                }
            }
            return ImportsGroup(rawContents, classes, packages)
        }

    }

}