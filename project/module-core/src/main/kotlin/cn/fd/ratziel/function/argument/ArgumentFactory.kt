package cn.fd.ratziel.function.argument

import cn.fd.ratziel.function.argument.exception.*

/**
 * ArgumentFactory - 参数工厂
 * 多参数情况的处理
 *
 * @author TheFloodDragon
 * @since 2024/5/1 13:19
 */
interface ArgumentFactory : ArgumentSupplier {

    /**
     * 重写以封装 [ArgumentNotFoundException]
     * @see [ArgumentSupplier.get]
     */
    @Throws(ArgumentSupplyException::class)
    override fun <T : Any> get(type: Class<T>) = try {
        pop(type)
    } catch (ex: Throwable) {
        throw ArgumentSupplyException(this, type, ex)
    }

    /**
     * 弹出第一个指定类型的参数
     * @throws ArgumentNotFoundException 当无法找到指定类型的参数时抛出
     */
    @Throws(ArgumentNotFoundException::class)
    fun <T : Any> pop(type: Class<T>): Argument<T>

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