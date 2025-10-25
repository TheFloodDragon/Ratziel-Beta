package cn.fd.ratziel.module.item.feature.action

import cn.fd.ratziel.common.block.BlockBuilder
import cn.fd.ratziel.common.block.ExecutableBlock
import cn.fd.ratziel.common.block.provided.ScriptBlock
import cn.fd.ratziel.core.Identifier
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.reactive.SimpleTrigger

/**
 * ItemTrigger
 *
 * @author TheFloodDragon
 * @since 2025/8/6 22:09
 */
open class ItemTrigger(vararg names: String) : SimpleTrigger(names) {

    init {
        // 默认绑定物品响应器
        bind(ItemResponder)
    }

    /**
     * 构建物品动作
     *
     * @param identifier 物品标识符
     * @param element 带有语句内容的元素
     */
    open fun build(identifier: Identifier, element: Element): ExecutableBlock = build(element)

    companion object {

        @JvmStatic
        fun build(element: Element) = BlockBuilder.build(element) {
            // 关闭复制上下文, 原因: 上下文就用一次
            copyContext = false
            // 关闭显示脚本, 库库解析字符串为脚本
            options[ScriptBlock.EXPLICIT_PARSE_OPTION] = false
        }

    }

}