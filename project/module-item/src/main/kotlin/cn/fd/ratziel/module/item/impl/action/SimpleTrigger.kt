package cn.fd.ratziel.module.item.impl.action

import cn.fd.ratziel.core.Identifier
import cn.fd.ratziel.module.item.api.action.ItemTrigger
import cn.fd.ratziel.module.script.block.BlockBuilder
import kotlinx.serialization.json.JsonElement

/**
 * SimpleTrigger
 *
 * @author TheFloodDragon
 * @since 2025/5/2 12:48
 */
open class SimpleTrigger(
    /**
     * 触发器名称
     */
    override val name: String,
    /**
     * 触发器别名
     */
    vararg val alias: String,
) : ItemTrigger {
    override fun build(identifier: Identifier, element: JsonElement) = BlockBuilder.build(element)
    override fun toString() = "SimpleTrigger(name=$name, alias=${alias.contentToString()})"
}

/**
 * 注册简易的 [ItemTrigger] - [SimpleTrigger]
 */
fun registerTrigger(name: String, vararg alias: String): SimpleTrigger {
    val simple = SimpleTrigger(name, *alias)
    registerTrigger(simple)
    return simple
}

/**
 * 注册简易的 [ItemTrigger] - [SimpleTrigger]
 */
fun registerTrigger(trigger: SimpleTrigger): SimpleTrigger {
    // 注册主名
    ActionManager.registry.put(trigger.name, trigger)
    // 注册别名
    for (alia in trigger.alias) {
        ActionManager.registry.put(alia, trigger)
    }
    return trigger
}