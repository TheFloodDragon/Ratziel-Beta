package cn.fd.ratziel.module.item.util

import cn.fd.ratziel.module.item.command.inferEquipmentSlot
import cn.fd.ratziel.module.item.nbt.NBTCompound
import cn.fd.ratziel.module.item.nms.RefItemStack
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import taboolib.type.BukkitEquipment
import java.util.function.Consumer


/**
 * 根据物品栏位获取物品
 * @param slot 栏位的字符串形式, 可以是数字也可是特殊栏位字符串
 */
fun PlayerInventory.getItemBySlot(slot: String?): ItemStack? {
    if (slot == null) {
        return this.getItem(BukkitEquipment.HAND.bukkit)
    } else {
        val es = inferEquipmentSlot(slot)
        return if (es == null) this.getItem(slot.toInt())
        else this.getItem(es.bukkit)
    }
}

/**
 * 根据物品栏位操作物品NBT数据
 */
fun PlayerInventory.handleItemTag(slot: String?, action: Consumer<NBTCompound>): NBTCompound? {
    val item = RefItemStack(getItemBySlot(slot) ?: return null)
    val tag = item.getTag() ?: return null
    action.accept(tag)
    item.setTag(tag)
    return tag
}