package cn.fd.ratziel.function.argument

import cn.fd.ratziel.function.argument.exception.*

/**
 * ArgumentFactory - 可变参数工厂
 *
 * @author TheFloodDragon
 * @since 2024/5/1 13:19
 */
interface ArgumentFactory {

    /**
     * 弹出第一个指定类型的参数
     * @throws ArgumentNotFoundException 当无法找到指定类型的参数时抛出
     */
    @Throws(ArgumentNotFoundException::class)
    fun <T : Any> pop(type: Class<T>): Argument<T>

    /**
     * 弹出第一个指定类型的参数
     * 若无法找到, 则返回空
     */
    fun <T : Any> popOrNull(type: Class<T>): Argument<T>?

    /**
     * 弹出第一个指定类型的参数
     * 若无法找到, 则返回默认值
     */
    fun <T : Any> popOr(type: Class<T>, default: T): Argument<T>

    /**
     * 弹出所有指定类型的参数
     */
    fun <T : Any> popAll(type: Class<T>): Iterable<Argument<T>>

    /**
     * 添加一个参数元素
     */
    fun add(element: Argument<*>): Boolean

    /**
     * 删除一个参数元素
     */
    fun remove(element: Argument<*>): Boolean

}