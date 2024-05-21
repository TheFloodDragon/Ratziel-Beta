package cn.fd.ratziel.module.item.api.common

import cn.fd.ratziel.function.argument.ArgumentFactory
import cn.fd.ratziel.function.argument.DefaultArgumentFactory
import cn.fd.ratziel.module.item.api.Resolver

/**
 * ArgumentResolver
 *
 * @author TheFloodDragon
 * @since 2024/5/21 22:54
 */
interface ArgumentResolver<E, T> : Resolver<E, T> {

    /**
     * 解析元素 (带参数)
     */
    fun resolve(element: E, arguments: ArgumentFactory): T

    /**
     * 解析元素 (带空参数)
     */
    override fun resolve(element: E): T = resolve(element, DefaultArgumentFactory())

}