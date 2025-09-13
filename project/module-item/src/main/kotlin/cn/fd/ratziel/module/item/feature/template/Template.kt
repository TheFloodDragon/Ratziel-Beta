package cn.fd.ratziel.module.item.feature.template

import cn.fd.ratziel.core.element.Element
import taboolib.common.platform.function.warning

/**
 * Template
 *
 * @author TheFloodDragon
 * @since 2025/8/6 22:33
 */
data class Template(
    /**
     * 模板的原始元素
     */
    val element: Element,
    /**
     * 父模板
     */
    val parents: Collection<String> = emptySet(),
) {

    /**
     * 模板名称
     */
    val name get() = element.name

    /**
     * 分析模板继承关系
     *
     * @return 不翻转时从底部开始
     */
    fun asChain(): List<Template> {
        // 没有父模板直接返回
        if (parents.isEmpty()) return listOf(this)

        val stack = ArrayList<Template>()
        var warned = false // 是否被警告过
        var last: Template = this
        stack.add(last) // 先加我自己

        val iteratorStack = mutableListOf(last.parents.iterator())
        while (iteratorStack.isNotEmpty()) {
            // 取栈顶迭代器
            val iterator = iteratorStack.last()
            // 迭代器还没迭代完
            if (iterator.hasNext()) {
                // 取下一个父模板 (找不到就跳过)
                val parent = TemplateElement.findBy(iterator.next()) ?: continue
                // 链式引用截断机制
                if (stack.contains(parent)) {
                    warning("Circular inheritance detected! $last wants to inherit the exist template $parent!")
                    warned = true
                } else {
                    last = parent
                    stack.add(last)
                    // 取父模板的迭代器, 入栈
                    if (parent.parents.isNotEmpty())
                        iteratorStack.add(parent.parents.iterator())
                }
            } else {
                // 迭代完了, 出栈
                iteratorStack.remove(iterator)
            }
        }
        if (warned) warning("Template chain has been truncated! Final Chain: ${stack.map { it.name }}")
        return stack
    }

    override fun toString() = "Template(name=${element.name}, parents=$parents)"

}