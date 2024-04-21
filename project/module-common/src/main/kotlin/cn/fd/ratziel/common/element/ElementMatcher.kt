package cn.fd.ratziel.common.element

import cn.fd.ratziel.core.element.ElementType
import cn.fd.ratziel.core.element.service.ElementRegistry

/**
 * ElementMatcher
 *
 * @author: TheFloodDragon
 * @since 2023/8/23 10:55
 */
object ElementMatcher {

    const val SEPARATOR = ':'

    /**
     * 元素类型表达式匹配
     * 表达式格式:
     *    命名空间:类型名称(或别名)
     */
    fun matchType(expression: String): ElementType {
        val split = expression.split(SEPARATOR)
        return when {
            // 命名空间:类型名称(或别名)
            split.size >= 2 -> matchAll(split[0], split[1])
            // 类型名称(或别名)
            split.size == 1 -> matchName(split[0])
            // 错误的表达式
            else -> null
        } ?: error("Unknown element type: \"$expression\" !")
    }

    /**
     * 匹配名称和命名空间
     */
    fun matchAll(space: String, name: String): ElementType? = matchName(name, matchSpace(space).toSet())

    /**
     * 匹配命名空间
     * @param space 命名空间
     * @param types 元素类型列表
     */
    fun matchSpace(
        space: String,
        types: Set<ElementType> = ElementRegistry.getAllElementTypes(),
    ): List<ElementType> = types.filter { it.space == space }

    /**
     * 匹配类型名称
     * @param name 目标名称
     * @param types 元素类型列表
     */
    fun matchName(
        name: String,
        types: Set<ElementType> = ElementRegistry.getAllElementTypes(),
    ): ElementType? = types.find { it.appellations.contains(name) }


}