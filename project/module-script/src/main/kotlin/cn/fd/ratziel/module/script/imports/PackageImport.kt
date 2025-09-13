package cn.fd.ratziel.module.script.imports

import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap

/**
 * PackageImport - 包导入
 *
 * @author TheFloodDragon
 * @since 2025/8/11 15:07
 */
data class PackageImport(
    /**
     * 包名称
     */
    val packageName: String,
) {

    /**
     * 包中的类缓存
     */
    private val classesCache = ConcurrentHashMap<String, WeakReference<Class<*>>>()

    /**
     * 搜索包中类
     */
    fun search(name: String): Class<*>? {
        val cached = classesCache[name]?.get()
        if (cached != null) return cached
        try {
            val find = Class.forName("$packageName.$name", false, this::class.java.classLoader)
            classesCache[name] = WeakReference(find)
            return find
        } catch (_: ClassNotFoundException) {
            return null
        }
    }

    override fun toString() = "$packageName.*"

}