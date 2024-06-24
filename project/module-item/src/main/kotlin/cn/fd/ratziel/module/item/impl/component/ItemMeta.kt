@file:OptIn(ExperimentalSerializationApi::class)

package cn.fd.ratziel.module.item.impl.component

import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.ItemMaterial
import cn.fd.ratziel.module.item.api.ItemTransformer
import cn.fd.ratziel.module.item.impl.OccupyNode
import cn.fd.ratziel.module.item.impl.TheItemData
import cn.fd.ratziel.module.item.util.toApexComponent
import cn.fd.ratziel.module.item.util.toApexData
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

/**
 * VItemMeta
 *
 * @author TheFloodDragon
 * @since 2024/4/4 20:10
 */
@Serializable
data class ItemMeta(
    @JsonNames("mat", "mats", "materials")
    var material: ItemMaterial = ItemMaterial.EMPTY,
    var display: ItemDisplay = ItemDisplay(),
    var durability: ItemDurability = ItemDurability(),
    var sundry: ItemSundry = ItemSundry()
) {

    companion object : ItemTransformer<ItemMeta> {

        override val node = OccupyNode.APEX_NODE

        override fun detransform(data: ItemData) = ItemMeta(
            material = data.material,
            display = ItemDisplay.toApexComponent(data),
            durability = ItemDurability.toApexComponent(data),
            sundry = ItemSundry.toApexComponent(data),
        )

        override fun transform(component: ItemMeta): ItemData {
            val data = TheItemData()
            data.material = component.material
            TheItemData.mergeShallow(data, ItemDisplay.toApexData(component.display))
            TheItemData.mergeShallow(data, ItemDurability.toApexData(component.durability))
            TheItemData.mergeShallow(data, ItemSundry.toApexData(component.sundry))
            return data
        }

    }

}