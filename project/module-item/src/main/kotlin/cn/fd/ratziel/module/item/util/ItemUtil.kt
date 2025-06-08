package cn.fd.ratziel.module.item.util

import cn.altawk.nbt.tag.NbtCompound
import cn.fd.ratziel.module.item.internal.nms.RefItemStack
import org.bukkit.inventory.ItemStack
import taboolib.platform.util.isAir
import java.util.function.Consumer

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