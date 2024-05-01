package cn.fd.ratziel.function.argument

/**
 * ArgumentQueue - 参数队列
 *
 * @author TheFloodDragon
 * @since 2024/5/1 13:19
 */
interface ArgumentQueue {

    /**
     * 弹出一个指定类型参数
     * @throws ArgumentNotFoundException 无法找到参数
     */
    fun <T> pop(type: Class<T>): Argument<out T>

    /**
     * 参数所有指定类型的参数
     */
    fun <T> popAll(type: Class<T>): Iterable<Argument<out T>>

}