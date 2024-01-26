package cn.fd.ratziel.module.itemengine.item.meta

import cn.fd.ratziel.module.itemengine.api.attribute.ItemAttribute
import cn.fd.ratziel.module.itemengine.api.part.meta.ItemMetadata
import cn.fd.ratziel.module.itemengine.item.builder.DefaultItemSerializer
import cn.fd.ratziel.module.itemengine.nbt.NBTTag
import cn.fd.ratziel.module.itemengine.util.detransformFrom
import cn.fd.ratziel.module.itemengine.util.transformTo
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
) : ItemMetadata, ItemAttribute<VItemMeta> {

    override fun detransform(input: NBTTag) {
        display.detransformFrom(input)
        characteristic.detransformFrom(input)
        nbt.merge(input.clone().apply { DefaultItemSerializer.usedNodes.forEach { remove(it) } })
    }

    override fun transform(source: NBTTag) = source.also { tag ->
        display.transformTo(tag)
        characteristic.transformTo(tag)
        tag.merge(nbt)
    }

}