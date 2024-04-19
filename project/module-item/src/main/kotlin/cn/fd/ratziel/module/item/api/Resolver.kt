package cn.fd.ratziel.module.item.api

/**
 * Resolver - 解析器
 *
 * @author TheFloodDragon
 * @since 2024/4/19 20:57
 */
interface Resolver<T, R> {

    /**
     * 解析[T]为[R]
     */
    fun resolve(target: T): R

}