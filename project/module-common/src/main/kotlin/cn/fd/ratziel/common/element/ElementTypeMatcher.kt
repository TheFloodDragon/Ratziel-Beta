package cn.fd.ratziel.common.element

import cn.fd.ratziel.core.element.service.ElementRegistry
import cn.fd.ratziel.core.element.ElementType
import cn.fd.ratziel.core.util.quickFuture
import java.util.concurrent.CompletableFuture

/**
 * ElementTypeMather
 *
 * @author: TheFloodDragon
 * @since 2023/8/23 10:55
 */
object ElementTypeMatcher {

    const val SEPARATOR = ':'

    /**
     * 元素类型表达式匹配
     * 表达式格式:
     *    命名空间:类型名称(或别名)
     */
    fun match(expression: String): ElementType? {
        val split = expression.split(SEPARATOR)
        return when (split.size) {
            // 命名空间:类型名称(或别名)
            2 -> matchAll(split[0], split[1]).get()
            // 类型名称(或别名)
            1 -> matchName(split[0]).get()
            //错误的表达式
            else -> null
        }
    }

    /**
     * 匹配名称和命名空间
     */
    fun matchAll(space: String, name: String): CompletableFuture<ElementType?> {
        return matchName(name, matchSpace(space).get().toSet())
    }

    /**
     * 匹配命名空间
     * @param space 命名空间
     * @param types 元素类型列表
     */
    fun matchSpace(
        space: String,
        types: Set<ElementType> = ElementRegistry.getAllElementTypes(),
    ): CompletableFuture<List<ElementType>> {
        return quickFuture {
            types.filter { it.space == space }
        }
    }

    /**
     * 匹配类型名称
     * @param name 目标名称
     * @param types 元素类型列表
     */
    fun matchName(
        name: String,
        types: Set<ElementType> = ElementRegistry.getAllElementTypes(),
    ): CompletableFuture<ElementType?> {
        return quickFuture {
            types.find { it.appellations.contains(name) }
        }
    }


}