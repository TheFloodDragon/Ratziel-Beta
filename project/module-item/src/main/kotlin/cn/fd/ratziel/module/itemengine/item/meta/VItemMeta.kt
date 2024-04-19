package cn.fd.ratziel.module.itemengine.item.meta

import cn.fd.ratziel.module.itemengine.api.attribute.ItemAttribute
import cn.fd.ratziel.module.itemengine.api.attribute.NBTTransformer
import cn.fd.ratziel.module.itemengine.api.part.meta.ItemMetadata
import cn.fd.ratziel.module.itemengine.nbt.NBTTag
import cn.fd.ratziel.module.itemengine.util.applyFrom
import cn.fd.ratziel.module.itemengine.util.applyTo
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

    override val transformer get() = Companion

    companion object : NBTTransformer<VItemMeta> {

        override fun detransform(target: VItemMeta, from: NBTTag): Unit = target.run {
            display.applyTo(from)
            characteristic.applyTo(from)
//            nbt.merge(from.clone().apply { DefaultItemSerializer.usedNodes.forEach { remove(it) } })
        }

        override fun transform(target: VItemMeta, source: NBTTag): NBTTag = target.run {
            source.also { tag ->
                display.applyFrom(tag)
                characteristic.applyFrom(tag)
                tag.merge(nbt)
            }
        }

    }

}