package cn.fd.ratziel.module.item.feature.template

import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.serialization.json.JsonTree
import kotlinx.serialization.json.JsonObject
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
    val origin: Element,
    /**
     * 父模板
     */
    val parents: Collection<String> = emptySet(),
) {

    /**
     * 模板名称
     */
    val name get() = origin.name

    /**
     * 模板的当前元素内容 (继承了父模板后的)
     */
    val element: JsonObject by lazy {
        // 模板的元素在最开始解析时经过校验, 所以直接转 ObjectNode 就行
        val source = JsonTree(origin.property).root as JsonTree.ObjectNode
        // 从底部开始合并 (不替换底部元素的一个个往上合并)
        for (name in parents) {
            // 取父模板 (找不到就跳过)
            val template = TemplateElement.findBy(name) ?: continue
            // 获取模板的元素
            val target = template.element
            // 不替换原有的合并
            TemplateParser.merge(source, target)
        }
        // 换成不可变类型
        JsonTree.parseToElement(source) as JsonObject
    }

    /**
     * 模板依赖链 (不翻转时从底部开始)
     */
    val dependencyChain: List<Template> by lazy {
        // 没有父模板直接返回
        if (parents.isEmpty()) return@lazy listOf(this)

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
        if (warned) warning("Template chain has been truncated! Final Chain: ${stack.joinToString(" -> ") { it.name }}")
        return@lazy stack
    }

    override fun toString() = "Template(name=$name, parents=$parents)"

}