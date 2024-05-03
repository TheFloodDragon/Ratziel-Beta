package cn.fd.ratziel.function.argument

import cn.fd.ratziel.function.argument.exception.ArgumentSupplyException

/**
 * SuppliableArgument
 *
 * @author TheFloodDragon
 * @since 2024/5/2 21:42
 */
abstract class SuppliableArgument<T : Any>(value: T) : SingleArgument<T>(value), ArgumentSupplier {

    /**
     * 提供对应参数的值
     */
    abstract fun <K> supply(type: Class<K>): K

    /**
     * 重写以封装异常
     * @return 一个新的参数
     */
    override fun <K : Any> get(type: Class<K>): Argument<K> = try {
        SingleArgument(supply(type))
    } catch (ex: Throwable) {
        throw ArgumentSupplyException(this, type, ex)
    }

    override fun toString() = "SuppliableArgument(value=$value, type=$type)"

}