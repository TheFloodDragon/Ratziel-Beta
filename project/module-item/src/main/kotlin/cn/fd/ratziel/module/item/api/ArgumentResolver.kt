package cn.fd.ratziel.module.item.api

import cn.fd.ratziel.function.ArgumentContext
import cn.fd.ratziel.function.SimpleArgumentContext

/**
 * ArgumentResolver
 *
 * @author TheFloodDragon
 * @since 2024/5/21 22:54
 */
interface ArgumentResolver<E, T> {

    /**
     * 解析元素 (带参数)
     */
    fun resolve(element: E, context: ArgumentContext): T

    /**
     * 解析元素 (不带参数)
     */
    fun resolve(element: E): T = resolve(element, SimpleArgumentContext())

}