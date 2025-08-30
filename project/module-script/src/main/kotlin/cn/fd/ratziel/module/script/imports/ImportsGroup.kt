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