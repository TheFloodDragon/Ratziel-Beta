@file:OptIn(ExperimentalSerializationApi::class)

package cn.fd.ratziel.module.item.impl.component

import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.ItemMaterial
import cn.fd.ratziel.module.item.api.ItemNode
import cn.fd.ratziel.module.item.api.ItemTransformer
import cn.fd.ratziel.module.item.nbt.NBTCompound
import cn.fd.ratziel.module.item.util.toApexComponent
import cn.fd.ratziel.module.item.util.toApexData
import kotlinx.serialization.Contextual
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

/**
 * ItemMetadata
 *
 * @author TheFloodDragon
 * @since 2024/4/4 20:10
 */
@Serializable
data class ItemMetadata(
    /**
     * 物品材料
     */
    
     @JsonNames("mat", "mats", "materials")
    var material: ItemMaterial = ItemMaterial.EMPTY,
    /**
     * 物品耐久部分
     */
    var display: ItemDisplay = ItemDisplay(),
    /**
     * 物品耐久部分
     */
    var durability: ItemDurability = ItemDurability(),
    /**
     * 物品杂项部分
     */
    var sundry: ItemSundry = ItemSundry(),
    /**
     * 物品特征部分
     */
    var characteristic: ItemCharacteristic = ItemCharacteristic(),
    /**
     * 物品数据标签
     */
    @JsonNames("nbt", "nbt-data", "nbt-tag")
    var tag: @Contextual NBTCompound? = null
) {

    companion object : ItemTransformer<ItemMetadata> {

        override val node = ItemNode.ROOT

        override fun transform(data: ItemData, component: ItemMetadata) {
            data.material = component.material
            ItemDisplay.toApexData(component.display, data)
            ItemDurability.toApexData(component.durability, data)
            ItemSundry.toApexData(component.sundry, data)
            ItemCharacteristic.toApexData(component.characteristic, data)
            val newTag = component.tag
            if (newTag != null) data.tag.merge(newTag, true)
        }

        override fun detransform(data: ItemData) = ItemMetadata(
            material = data.material,
            display = ItemDisplay.toApexComponent(data),
            durability = ItemDurability.toApexComponent(data),
            sundry = ItemSundry.toApexComponent(data),
            characteristic = ItemCharacteristic.toApexComponent(data),
            tag = data.tag
        )

    }

}