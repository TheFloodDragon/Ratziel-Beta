package cn.fd.ratziel.module.script.importing

import java.lang.ref.WeakReference

/**
 * ClassImportation - 类导入件
 *
 * @author TheFloodDragon
 * @since 2025/8/11 15:07
 */
data class ClassImportation(
    /**
     * 类全名
     */
    val fullName: String,
) : Importation {

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

    override fun toString() = this.fullName

}