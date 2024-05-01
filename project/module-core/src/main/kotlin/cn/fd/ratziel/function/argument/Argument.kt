package cn.fd.ratziel.function.argument

/**
 * Argument - 参数
 *
 * @author TheFloodDragon
 * @since 2024/5/1 13:18
 */
interface Argument<T> {

    /**
     * 参数类型
     */
    val type: Class<*>

    /**
     * 参数的值
     */
    val value: T

}