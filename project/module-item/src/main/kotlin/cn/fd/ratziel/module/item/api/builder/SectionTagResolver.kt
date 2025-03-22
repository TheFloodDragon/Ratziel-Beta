package cn.fd.ratziel.module.item.api.builder

import cn.fd.ratziel.core.function.SimpleArgumentContext
import cn.fd.ratziel.function.ArgumentContext

/**
 * SectionTagResolver
 *
 * @author TheFloodDragon
 * @since 2024/8/13 14:44
 */
interface SectionTagResolver {

    /**
     * 解析器名称
     */
    val names: Array<String>

    /**
     * 解析元素 (带参数)
     */
    fun resolve(element: List<String>, context: ArgumentContext): String?

    /**
     * 解析元素 (不带参数)
     */
    fun resolve(element: List<String>): String? = resolve(element, SimpleArgumentContext())

}