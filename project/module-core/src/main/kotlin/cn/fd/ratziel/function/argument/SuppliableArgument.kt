package cn.fd.ratziel.function.argument

/**
 * SuppliableArgument
 *
 * @author TheFloodDragon
 * @since 2024/5/2 21:42
 */
abstract class SuppliableArgument<T : Any>(value: T, type: Class<out T>) : SingleArgument<T>(value, type), ArgumentSupplier {

    constructor(value: T) : this(value, value::class.java)

    abstract fun <T> supply(type: Class<T>): T

    /**
     * 重写以封装异常
     */
    override fun <T> get(type: Class<T>): T = try {
        supply(type)
    } catch (ex: Throwable) {
        throw ArgumentSupplyException(this, type, ex)
    }

    /**
     * 简便方法 - 用于表示类型已受过检查
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> uncheck(target: Any): T = target as T

    override fun toString() = "SuppliableArgument(value=$value, type=$type)"

}