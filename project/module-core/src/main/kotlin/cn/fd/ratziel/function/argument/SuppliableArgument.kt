package cn.fd.ratziel.function.argument

import cn.fd.ratziel.function.argument.exception.ArgumentSupplyException

/**
 * SuppliableArgument
 *
 * @author TheFloodDragon
 * @since 2024/5/2 21:42
 */
abstract class SuppliableArgument<V : Any>(value: V) : SingleArgument<V>(value), ArgumentSupplier {

    /**
     * 提供对应参数的值
     */
    abstract fun <T : Any> supply(type: Class<T>): Argument<T>

    /**
     * 重写以封装异常
     * @return 一个新的参数
     */
    override fun <T : Any> get(type: Class<T>): Argument<T> = try {
        supply(type)
    } catch (ex: Throwable) {
        throw ArgumentSupplyException(this, type, ex)
    }

    override fun toString() = "SuppliableArgument(value=$value, type=$type)"

}