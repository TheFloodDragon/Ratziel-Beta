package cn.fd.ratziel.item.nms.deprecated.api

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
    protected var obj: Any? = null,
) {

    /**
     * 获取实例对象
     */
    abstract fun get(): Any?

    /**
     * 运行环境下的类
     */
    protected abstract val clazz: Class<*>

    /**
     * 类型检查
     */
    protected var typeCheck = false

    init {
        if (typeCheck)
            checkType()
    }

    fun checkType() {
        // 如果对象存在并且不符合类型要求
        if (obj != null && !obj!!::class.java.isInstance(clazz)) {
            throw NotCorrectClass(obj!!, clazz)
        }
    }

}