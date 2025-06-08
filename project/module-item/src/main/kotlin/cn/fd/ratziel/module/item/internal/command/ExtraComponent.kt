@file:Suppress("SpellCheckingInspection")

package cn.fd.ratziel.module.item.internal.command

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.command.component.CommandComponent
import taboolib.common.platform.command.component.CommandComponentDynamic
import taboolib.common.platform.command.suggestUncheck
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
fun CommandComponentDynamic.suggestSlots(suggest: List<String> = emptyList()): CommandComponentDynamic = suggestUncheck {
    suggest.plus(
        PlayerInventorySlot.equipmentSlots.flatMap { it.names.toList() }
    )
}

/**
 * 标识玩家背包的一个栏位
 */
class PlayerInventorySlot private constructor(
    /**
     * 代表一个栏位
     */
    private val slot: Any,
    /**
     * 栏位名称
     */
    vararg val names: String,
) {

    /**
     * 从玩家背包中获取
     */
    fun getItemFrom(player: Player): ItemStack? {
        return if (slot is BukkitEquipment) {
            slot.getItem(player)
        } else {
            player.inventory.getItem(slot as Int)
        }
    }

    companion object {

        /**
         * 主手栏位
         */
        val MAIN_HAND = PlayerInventorySlot(BukkitEquipment.HAND, "main-hand", "main", "hand")

        /**
         * 佩戴栏位
         */
        val equipmentSlots by lazy {
            // 特殊栏位
            val array = arrayOf(
                MAIN_HAND,
                PlayerInventorySlot(BukkitEquipment.HEAD, "helmet", "head"),
                PlayerInventorySlot(BukkitEquipment.CHEST, "chestplate", "chest"),
                PlayerInventorySlot(BukkitEquipment.LEGS, "leggings", "legs"),
                PlayerInventorySlot(BukkitEquipment.FEET, "boots", "feet"),
            )
            // 副手处理
            if (MinecraftVersion.isHigherOrEqual(MinecraftVersion.V1_12)) {
                array + PlayerInventorySlot(BukkitEquipment.OFF_HAND, "off-hand", "off")
            } else array
        }

        /**
         * 所有栏位
         */
        val allSlots: Map<String, PlayerInventorySlot> by lazy {
            val slotList = equipmentSlots.toMutableList()

            // 通用栏位 (int)
            // 0-35 正常的栏位 | 100-103 装备栏*4 | -106 副手
            for (i in (0..35) + (100..103) + -106) {
                slotList.add(PlayerInventorySlot(i, i.toString()))
            }

            slotList.flatMap { it.names.map { name -> name to it } }.toMap()
        }

        /**
         * 推断 [slot] 为 [PlayerInventorySlot]
         */
        fun infer(slot: String) =
            allSlots[slot.trim()]
                ?: throw IllegalArgumentException("Cannot infer '$slot' to PlayerInventorySlot!")

    }

}
