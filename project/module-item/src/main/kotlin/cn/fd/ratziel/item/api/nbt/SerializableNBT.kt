package cn.fd.ratziel.item.api.nbt

import cn.fd.ratziel.bukkit.util.nbt.NBTTag

/**
 * SerializableNBT
 *
 * @author TheFloodDragon
 * @since 2023/10/27 21:07
 */
interface SerializableNBT {

    /**
     * 将NBT标签应用到已有NBT标签中
     */
    fun applyTo(tag: NBTTag)

}