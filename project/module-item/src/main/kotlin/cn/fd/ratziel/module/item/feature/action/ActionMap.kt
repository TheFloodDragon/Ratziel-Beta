package cn.fd.ratziel.module.item.feature.action

import cn.fd.ratziel.core.reactive.Trigger
import cn.fd.ratziel.module.script.block.ExecutableBlock

/**
 * ActionMap - 物品动作表
 *
 * @author TheFloodDragon
 * @since 2025/5/2 12:22
 */
class ActionMap(
    val map: Map<Trigger, ExecutableBlock>,
) {
    operator fun get(trigger: Trigger) = this.map[trigger]
}
