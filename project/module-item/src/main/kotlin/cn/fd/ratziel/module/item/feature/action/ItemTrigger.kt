package cn.fd.ratziel.module.item.feature.action

import cn.fd.ratziel.core.Identifier
import cn.fd.ratziel.core.reactive.SimpleTrigger
import cn.fd.ratziel.module.script.block.ExecutableBlock
import kotlinx.serialization.json.JsonElement

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
     * @param element 语句内容
     */
    abstract fun build(identifier: Identifier, element: JsonElement): ExecutableBlock

}