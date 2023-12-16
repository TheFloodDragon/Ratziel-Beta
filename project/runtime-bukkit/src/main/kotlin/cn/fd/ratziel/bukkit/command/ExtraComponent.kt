@file:Suppress("SpellCheckingInspection")

package cn.fd.ratziel.bukkit.command

import taboolib.common.platform.command.component.CommandComponent
import taboolib.common.platform.command.component.CommandComponentDynamic
import taboolib.common.platform.command.suggest


/**
 * 添加一层物品栏位节点（自动约束、自动建议）
 *
 * @param suggest 额外建议
 */
fun CommandComponent.slot(
    comment: String = "slot",
    suggest: List<String> = emptyList(),
    optional: Boolean = false,
    permission: String = "",
    dynamic: CommandComponentDynamic.() -> Unit = {},
) = dynamic(comment, optional, permission, dynamic).suggestSlots(suggest)

/**
 * 创建参数补全（仅物品栏位）
 *
 * @param suggest 额外建议
 */
fun CommandComponentDynamic.suggestSlots(suggest: List<String> = emptyList()): CommandComponentDynamic = suggest {
    mutableListOf(
        // 0 , -106 , 103 , 102 , 101 , 100 (均省略用以下字符串代替)
        "main-hand", "off-hand", "helmet", "chestplate", "leggings", "boots"
    ).apply {
        addAll(Array(35) { (it + 1).toString() }) // 1 到 35
    }
}

/**
 * 将物品栏位转换成栏位ID
 */
fun inferSlotToInt(slot: String) = when (slot) {
    "main-hand" -> 0
    "off-hand" -> -106
    "helmet" -> 103
    "chestplate" -> 102
    "leggings" -> 101
    "boots" -> 100
    else -> slot.toInt()
}