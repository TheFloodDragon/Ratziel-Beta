package cn.fd.ratziel.item.meta

import cn.fd.ratziel.item.api.ItemMetadata
import cn.fd.ratziel.item.api.nbt.ItemTagTranslator
import taboolib.module.nms.ItemTag
import taboolib.module.nms.ItemTagData

/**
 * VItemMeta
 *
 * @author TheFloodDragon
 * @since 2023/10/14 16:15
 */
open class VItemMeta(
    override var display: VItemDisplay = VItemDisplay(),
    override var characteristic: VItemCharacteristic = VItemCharacteristic(),
    override var durability: VItemDurability = VItemDurability(),
    override var nbt: ItemTag = ItemTag(),
) : ItemMetadata, ItemTagTranslator {

    override fun toItemTag(): ItemTagData {
        TODO("Not yet implemented")
    }

}