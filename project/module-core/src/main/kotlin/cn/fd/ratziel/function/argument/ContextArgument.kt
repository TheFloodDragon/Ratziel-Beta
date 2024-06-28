package cn.fd.ratziel.function.argument

import cn.fd.ratziel.function.argument.exception.ArgumentNotFoundException

/**
 * ContextArgument - 上下文参数
 *
 * 本质为一个参数容器
 *
 * @author TheFloodDragon
 * @since 2024/6/28 16:14
 */
interface ContextArgument {

    /**
     * 弹出第一个指定类型的参数
     * @throws ArgumentNotFoundException 当无法找到指定类型的参数时抛出
     */
    @Throws(ArgumentNotFoundException::class)
    fun <T> pop(type: Class<T>): T

    /**
     * 弹出第一个指定类型的参数
     * 若无法找到, 则返回空
     */
    fun <T> popOrNull(type: Class<T>): T?

    /**
     * 弹出第一个指定类型的参数
     * 若无法找到, 则返回默认值
     */
    fun <T> popOr(type: Class<T>, default: T): T

    /**
     * 弹出所有指定类型的参数
     */
    fun <T> popAll(type: Class<T>): Iterable<T>

    /**
     * 添加一个参数元素
     */
    fun add(element: Any): Boolean

    /**
     * 删除一个参数元素
     */
    fun remove(element: Any): Boolean

    /**
     * 获取所有参数
     */
    fun args(): Collection<Any>

}