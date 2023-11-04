package cn.fd.ratziel.module.item.item.meta

import cn.fd.ratziel.bukkit.util.nbt.NBTTag
import cn.fd.ratziel.module.item.api.meta.ItemMetadata

/**
 * VItemMeta
 *
 * @author TheFloodDragon
 * @since 2023/10/14 16:15
 */
data class VItemMeta(
    override var display: VItemDisplay = VItemDisplay(),
    override var characteristic: VItemCharacteristic = VItemCharacteristic(),
    override var durability: VItemDurability = VItemDurability(),
    override var nbt: NBTTag = NBTTag(),
) : ItemMetadata