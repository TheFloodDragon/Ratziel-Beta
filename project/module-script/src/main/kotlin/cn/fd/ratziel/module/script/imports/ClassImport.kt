package cn.fd.ratziel.module.script.imports

/**
 * ClassImport - 导入的类
 *
 * @author TheFloodDragon
 * @since 2025/8/11 15:07
 */
class ClassImport(
    val simpleName: String,
    val fullName: String,
) {

    constructor(name: String) : this(name.substringAfterLast('.'), name)

    val clazz by lazy {
        try {
            Class.forName(fullName, false, this::class.java.classLoader)
        } catch (_: ClassNotFoundException) {
            null
        }
    }

}