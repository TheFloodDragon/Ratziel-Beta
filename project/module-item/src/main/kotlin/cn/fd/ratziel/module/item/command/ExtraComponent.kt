@file:Suppress("SpellCheckingInspection")

package cn.fd.ratziel.module.item.command

import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import taboolib.common.platform.command.component.CommandComponent
import taboolib.common.platform.command.component.CommandComponentDynamic
import taboolib.common.platform.command.suggest
import taboolib.module.nms.MinecraftVersion
import taboolib.type.BukkitEquipment


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
    suggest.toMutableList().apply {
        add("main-hand")
        if (MinecraftVersion.isHigherOrEqual(MinecraftVersion.V1_12)) add("off-hand") // 副手特殊处理
        add("helmet")
        add("chestplate")
        add("leggings")
        add("boots")
    }
}

/**
 * 根据栏位字符串获取玩家物品
 */
fun getItemBySlot(slot: String, inventory: PlayerInventory): ItemStack? =
    runCatching { inferEquipmentSlot(slot) }.getOrNull()?.let {
        inventory.getItem(it.bukkit)
    } ?: inventory.getItem(slot.toInt())

fun inferEquipmentSlot(slot: String) = when (slot) {
    "main-hand", "main", "hand" -> BukkitEquipment.HAND
    "off-hand", "off" -> BukkitEquipment.OFF_HAND
    "helmet", "head" -> BukkitEquipment.HEAD
    "chestplate", "chest" -> BukkitEquipment.CHEST
    "leggings", "legs" -> BukkitEquipment.LEGS
    "boots", "feet" -> BukkitEquipment.FEET
    else -> error("Unkown Equipment Slot \"$slot\" !")
}