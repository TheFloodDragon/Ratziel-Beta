package cn.fd.ratziel.module.script.imports

/**
 * ImportGroup
 *
 * @author TheFloodDragon
 * @since 2025/8/11 15:06
 */
class ImportGroup(
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
        fun parse(rawContents: List<String>): ImportGroup {
            val classes = ArrayList<ClassImport>()
            val packages = ArrayList<PackageImport>()
            for (import in rawContents) {
                if (import.endsWith('*') || import.endsWith('.')) {
                    packages.add(PackageImport(import.substringBeforeLast('.')))
                } else {
                    classes.add(ClassImport(import))
                }
            }
            return ImportGroup(rawContents, classes, packages)
        }

    }

}