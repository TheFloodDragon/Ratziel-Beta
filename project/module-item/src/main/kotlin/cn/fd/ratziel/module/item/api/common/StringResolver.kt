package cn.fd.ratziel.module.item.api.common

import cn.fd.ratziel.function.argument.ArgumentFactory
import cn.fd.ratziel.function.argument.DefaultArgumentFactory
import cn.fd.ratziel.module.item.api.Resolver

/**
 * StringResolver
 *
 * @author TheFloodDragon
 * @since 2024/5/18 15:15
 */
interface StringResolver : Resolver<Iterable<String>, String?> {

    /**
     * 解析器名称
     */
    val name: String

    /**
     * 解析器别名
     */
    val alias: Array<String>

    /**
     * 解析元素 (带参数)
     */
    fun resolve(element: Iterable<String>, arguments: ArgumentFactory): String?

    /**
     * 解析元素 (带空参数)
     */
    override fun resolve(element: Iterable<String>): String? = resolve(element, DefaultArgumentFactory())

}