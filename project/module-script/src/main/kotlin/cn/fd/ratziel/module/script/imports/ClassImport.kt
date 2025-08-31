package cn.fd.ratziel.module.script.imports

import java.lang.ref.WeakReference

/**
 * ClassImport - 类导入
 *
 * @author TheFloodDragon
 * @since 2025/8/11 15:07
 */
data class ClassImport(
    /**
     * 类全名
     */
    val fullName: String,
) {

    /**
     * 类简单名称
     */
    val simpleName: String = fullName.substringAfterLast('.')

    /**
     * 实际类对象引用
     */
    private var reference: WeakReference<Class<*>>? = null

    /**
     * 获取类对象
     */
    @Synchronized
    fun get(): Class<*>? {
        val clazz = reference?.get()
        if (clazz != null) return clazz
        try {
            val find = Class.forName(fullName, false, this::class.java.classLoader)
            reference = WeakReference(find)
            return find
        } catch (_: ClassNotFoundException) {
            return null
        }
    }

}