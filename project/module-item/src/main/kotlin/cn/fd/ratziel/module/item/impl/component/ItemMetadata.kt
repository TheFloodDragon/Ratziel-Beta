@file:OptIn(ExperimentalSerializationApi::class)

package cn.fd.ratziel.module.item.impl.component

import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.ItemMaterial
import cn.fd.ratziel.module.item.api.ItemTransformer
import cn.fd.ratziel.module.item.impl.OccupyNode
import cn.fd.ratziel.module.item.impl.ItemDataImpl
import cn.fd.ratziel.module.item.util.toApexComponent
import cn.fd.ratziel.module.item.util.toApexData
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
    @JsonNames("mat", "mats", "materials")
    var material: ItemMaterial = ItemMaterial.EMPTY,
    var display: ItemDisplay = ItemDisplay(),
    var durability: ItemDurability = ItemDurability(),
    var sundry: ItemSundry = ItemSundry()
) {

    companion object : ItemTransformer<ItemMetadata> {

        override val node = OccupyNode.APEX_NODE

        override fun detransform(data: ItemData) = ItemMetadata(
            material = data.material,
            display = ItemDisplay.toApexComponent(data),
            durability = ItemDurability.toApexComponent(data),
            sundry = ItemSundry.toApexComponent(data),
        )

        override fun transform(component: ItemMetadata): ItemData {
            val data = ItemDataImpl()
            data.material = component.material
            ItemDataImpl.mergeShallow(data, ItemDisplay.toApexData(component.display))
            ItemDataImpl.mergeShallow(data, ItemDurability.toApexData(component.durability))
            ItemDataImpl.mergeShallow(data, ItemSundry.toApexData(component.sundry))
            return data
        }

    }

}