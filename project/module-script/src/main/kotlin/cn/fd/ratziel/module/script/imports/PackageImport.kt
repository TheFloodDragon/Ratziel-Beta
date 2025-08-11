package cn.fd.ratziel.module.script.imports

import java.util.concurrent.ConcurrentHashMap

/**
 * PackageImport - 导入的包
 *
 * @author TheFloodDragon
 * @since 2025/8/11 15:07
 */
class PackageImport(
    val pkgName: String,
) {

    private val classesCache = ConcurrentHashMap<String, Class<*>>()

    fun search(name: String): Class<*>? {
        val cached = classesCache[name]
        if (cached != null) return cached
        val find = try {
            Class.forName("$pkgName.$name", false, this::class.java.classLoader)
        } catch (_: ClassNotFoundException) {
            null
        }
        if (find != null) {
            classesCache[name] = find
            return find
        }
        return null
    }
}