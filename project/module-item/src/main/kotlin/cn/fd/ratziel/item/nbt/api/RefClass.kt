package cn.fd.ratziel.item.nbt.api

/**
 * RefClass - 镜像类
 * 相当于对某个类的包装
 * 用于多版本不同类的使用
 *
 * @author TheFloodDragon
 * @since 2023/10/21 21:40
 */
abstract class RefClass protected constructor(
    /**
     * 反射类对象
     */
    protected val obj: Any? = null,
) {

    init {
        checkType()
    }

    /**
     * 获取实例对象
     */
    abstract fun get() : Any?

    /**
     * 运行环境下的类
     */
    protected abstract val clazz: Class<*>

    /**
     * 类型检查
     */
    fun checkType() {
        // 如果对象存在并且不符合类型要求
        if (obj != null && !obj::class.java.isInstance(clazz)) {
            throw NotCorrectClass(obj, clazz)
        }
    }

}