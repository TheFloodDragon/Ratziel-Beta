package cn.fd.ratziel.module.item.util

import cn.fd.ratziel.module.item.command.inferEquipmentSlot
import cn.fd.ratziel.module.item.nbt.NBTCompound
import cn.fd.ratziel.module.item.reflex.RefItemStack
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import java.util.function.Consumer


/**
 * 根据物品栏位获取物品
 * @param slot 栏位的字符串形式, 可以是数字也可是特殊栏位字符串
 */
fun PlayerInventory.getItemBySlot(slot: String): ItemStack? = inferEquipmentSlot(slot)?.let { getItem(it.bukkit) } ?: getItem(slot.toInt())

/**
 * 根据物品栏位操作物品NBT数据
 */
fun PlayerInventory.editItemData(slot: String, action: Consumer<NBTCompound>): NBTCompound? {
    val item = RefItemStack(getItemBySlot(slot) ?: return null)
    val data = item.getData() ?: return null
    action.accept(data)
    item.setData(data)
    return data
}