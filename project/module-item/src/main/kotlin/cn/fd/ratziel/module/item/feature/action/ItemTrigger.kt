package cn.fd.ratziel.module.item.feature.action

import cn.fd.ratziel.core.Identifier
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.reactive.SimpleTrigger
import cn.fd.ratziel.module.script.block.ExecutableBlock

/**
 * ItemTrigger
 *
 * @author TheFloodDragon
 * @since 2025/8/6 22:09
 */
abstract class ItemTrigger(vararg names: String) : SimpleTrigger(names) {

    /**
     * 构建物品动作
     *
     * @param identifier 物品标识符
     * @param element 带有语句内容的元素
     */
    abstract fun build(identifier: Identifier, element: Element): ExecutableBlock

}