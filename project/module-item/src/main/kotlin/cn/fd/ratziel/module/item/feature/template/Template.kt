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
    val parent: Template? = null,
) {

    /**
     * 分析模板继承关系
     *
     * @return 不翻转时从底部开始
     */
    fun asChain(): List<Template> {
        val stack = ArrayList<Template>()
        var last: Template = this
        stack.add(last) // 先加我自己
        var parent: Template? = last.parent
        while (parent != null) {
            if (!stack.contains(parent)) {
                stack.add(parent) // 加父模板
                // 更新
                last = parent
                parent = last.parent
            } else {
                warning("Circular inheritance detected! $last wants to inherit the exist template $parent!")
                break
            }
        }
        return stack
    }

    override fun toString() = "Template(name=${element.name}, parent=${parent?.element?.name})"

}