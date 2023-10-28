package cn.fd.ratziel.item.api

import cn.fd.ratziel.bukkit.nbt.NBTTag

/**
 * ItemStream - 用于构建物品数据的流
 * 一个物品流实际上就是NBT生成的过程
 *
 * @author TheFloodDragon
 * @since 2023/10/27 20:36
 */
@Deprecated("意义不明")
abstract class ItemStream(
    /**
     * 原始NBT标签
     */
    val nbtTag: NBTTag = NBTTag(),
)