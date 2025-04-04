package cn.fd.ratziel.module.item.impl.builder

import cn.fd.ratziel.core.function.ArgumentContext

/**
 * SectionTagResolver
 *
 * @author TheFloodDragon
 * @since 2024/8/13 14:44
 */
abstract class SectionTagResolver(
    /**
     * 解析器名称
     */
    vararg val names: String
) {

    /**
     * 解析元素
     */
    abstract fun resolve(element: List<String>, context: ArgumentContext): String?

}