package cn.fd.ratziel.module.item.util

import cn.altawk.nbt.tag.NbtCompound
import cn.fd.ratziel.module.item.internal.command.inferEquipmentSlot
import cn.fd.ratziel.module.item.internal.nms.RefItemStack
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import taboolib.platform.util.isAir
import java.util.function.Consumer


/**
 * 根据物品栏位获取物品
 * @param slot 栏位的字符串形式, 可以是数字也可是特殊栏位字符串
 */
fun PlayerInventory.getItemBySlot(slot: String): ItemStack? {
    val es = inferEquipmentSlot(slot)
    return if (es == null) this.getItem(slot.toInt())
    else this.getItem(es.bukkit)
}

/**
 * 根据物品栏位操作物品NBT数据
 */
fun ItemStack.modifyTag(action: Consumer<NbtCompound>): NbtCompound? {
    if (this.isAir()) return null
    // 修改物品标签数据
    val ref = RefItemStack.of(this)
    val tag = ref.tag
    action.accept(tag)
    ref.tag = tag // 写回数据
    return tag
}