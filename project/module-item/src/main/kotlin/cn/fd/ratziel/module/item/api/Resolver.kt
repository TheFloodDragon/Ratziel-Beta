package cn.fd.ratziel.module.item.api

/**
 * Resolver - 解析器
 *
 * @author TheFloodDragon
 * @since 2024/4/19 20:57
 */
interface Resolver<E, T> {

    /**
     * 解析 [E] 为 [T]
     */
    fun resolve(element: E): T

}