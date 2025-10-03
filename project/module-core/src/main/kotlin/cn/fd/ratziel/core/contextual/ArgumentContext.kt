package cn.fd.ratziel.core.contextual


/**
 * ArgumentContext - 参数上下文 (本质为一个参数容器)
 *
 * @author TheFloodDragon
 * @since 2024/7/16 11:56
 */
interface ArgumentContext {

    /**
     * 弹出指定类型的参数
     *
     * @throws ArgumentNotFoundException 当无法找到指定类型的参数时抛出
     */
    @Throws(ArgumentNotFoundException::class)
    fun <T : Any> pop(type: Class<T>): T {
        return popOrNull(type) ?: throw ArgumentNotFoundException(type)
    }

    /**
     * 弹出指定类型的参数
     * 若无法找到, 则返回空
     */
    fun <T : Any> popOrNull(type: Class<T>): T?

    /**
     * 弹出指定类型的参数
     * 若无法找到, 则返回默认值
     */
    fun <T : Any> popOr(type: Class<T>, def: () -> T): T {
        val obj = this.popOrNull<T>(type)
        return obj ?: def()
    }

    /**
     * 弹出指定类型的参数
     * 若无法找到, 则添加默认值
     */
    fun <T : Any> popOrPut(type: Class<T>, def: () -> T): T {
        var obj = this.popOrNull<T>(type)
        if (obj == null) {
            obj = def()
            this.put(obj)
        }
        return obj
    }

    /**
     * 添加一个参数
     */
    fun put(element: Any)

    /**
     * 添加多个参数
     */
    fun putAll(elements: Iterable<Any>) {
        for (element in elements) put(element)
    }

    /**
     * 删除一个参数
     */
    fun remove(type: Class<*>)

    /**
     * 复制一份新的参数上下文
     */
    fun copy(): ArgumentContext

    /**
     * 获取所有参数
     */
    fun args(): Iterable<Any>

}