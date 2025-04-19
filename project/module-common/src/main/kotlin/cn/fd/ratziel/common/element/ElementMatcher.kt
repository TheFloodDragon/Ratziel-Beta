package cn.fd.ratziel.common.element

import cn.fd.ratziel.common.element.registry.ElementRegistry
import cn.fd.ratziel.core.element.ElementType

/**
 * ElementMatcher
 *
 * @author: TheFloodDragon
 * @since 2023/8/23 10:55
 */
object ElementMatcher {

    /**
     * 元素类型表达式匹配
     * 表达式格式:
     *    命名空间:类型名称(或别名)
     */
    fun matchType(expression: String): ElementType {
        return matchTypeOrNull(expression) ?: error("Unknown element type: \"$expression\" !")
    }

    /**
     * 元素类型表达式匹配
     * 表达式格式:
     *    命名空间:类型名称(或别名)
     */
    fun matchTypeOrNull(expression: String): ElementType? {
        val split = expression.split(':')
        return when {
            // 命名空间:类型名称(或别名)
            split.size >= 2 -> match(split[0], split[1])
            // 类型名称(或别名)
            split.size == 1 -> matchName(split[0])
            // 错误的表达式
            else -> null
        }
    }

    /**
     * 匹配名称和命名空间
     */
    fun match(space: String, name: String): ElementType? = matchName(name, matchSpace(space))

    /**
     * 匹配命名空间
     * @param space 命名空间
     * @param types 元素类型列表
     */
    fun matchSpace(space: String): List<ElementType> {
        return ElementRegistry.registry.keys.filter { it.space == space }
    }

    /**
     * 匹配类型名称
     * @param name 目标名称
     * @param types 从中查找
     */
    fun matchName(
        name: String,
        types: Iterable<ElementType> = ElementRegistry.registry.keys,
    ): ElementType? {
        return types.find { it.name == name || it.alias.contains(name) }
    }

}