package cn.fd.ratziel.module.itemengine.item.meta

import cn.fd.ratziel.module.itemengine.api.part.meta.ItemMetadata
import cn.fd.ratziel.module.itemengine.nbt.NBTTag
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

/**
 * VItemMeta
 *
 * @author TheFloodDragon
 * @since 2023/10/14 16:15
 */
@Serializable
data class VItemMeta(
    override var display: VItemDisplay = VItemDisplay(),
    override var characteristic: VItemCharacteristic = VItemCharacteristic(),
    override var durability: VItemDurability = VItemDurability(),
    override var nbt: @Contextual NBTTag = NBTTag(),
) : ItemMetadata