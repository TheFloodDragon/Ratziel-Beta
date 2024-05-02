package cn.fd.ratziel.function.argument

/**
 * ArgumentFactory - 参数工厂
 * 多参数情况的处理
 *
 * @author TheFloodDragon
 * @since 2024/5/1 13:19
 */
interface ArgumentFactory : ArgumentSupplier {

    override fun <T> get(type: Class<T>): T = try {
        pop(type)
    } catch (ex: Throwable) {
        throw ArgumentSupplyException(this, type, ex)
    }

    /**
     * 弹出一个指定类型的参数
     * @throws ArgumentNotFoundException 当无法找到指定类型的参数时抛出
     */
    fun <T> pop(type: Class<T>): T

    /**
     * 弹出一个指定类型的参数
     * 异常时返回默认值
     */
    fun <T> popOr(type: Class<T>, default: T): T

    /**
     * 弹出一个指定类型的参数
     * 异常时返回为空
     */
    fun <T> popOrNull(type: Class<T>): T?

    /**
     * 弹出所有指定类型的参数
     */
    fun <T> popAll(type: Class<T>): Iterable<T>

    /**
     * 添加一个参数
     */
    fun addArg(argument: Argument<*>): Boolean

    /**
     * 删除一个参数
     */
    fun removeArg(argument: Argument<*>): Boolean

}