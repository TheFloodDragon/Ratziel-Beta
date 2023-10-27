package cn.fd.ratziel.item.meta

import cn.fd.ratziel.item.api.meta.ItemMetadata
import taboolib.module.nms.ItemTag

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
    override var nbt: ItemTag = ItemTag(),
) : ItemMetadata