package cn.fd.ratziel.module.item.api

import cn.altawk.nbt.tag.NbtCompound


/**
 * StackData
 *
 * @author TheFloodDragon
 * @since 2024/8/31 19:53
 */
interface StackData : ItemData {

    /**
     * 物品自定义标签数据
     */
    var customTag: NbtCompound?

    /**
     * NMS的ItemStack源 - [NMSItemStack]
     */
    val nmsStack: Any?

    /**
     * Bukkit的ItemStack源
     */
    val bukkitStack: BukkitItemStack

}

typealias BukkitItemStack = org.bukkit.inventory.ItemStack
typealias NMSItemStack = net.minecraft.world.item.ItemStack