@file:OptIn(ExperimentalSerializationApi::class)

package cn.fd.ratziel.module.item.impl.part

import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.ItemMaterial
import cn.fd.ratziel.module.item.api.common.OccupyNode
import cn.fd.ratziel.module.item.api.part.ItemDisplay
import cn.fd.ratziel.module.item.api.part.ItemDurability
import cn.fd.ratziel.module.item.api.part.ItemMetadata
import cn.fd.ratziel.module.item.api.part.ItemSundry
import cn.fd.ratziel.module.item.util.accept
import cn.fd.ratziel.module.item.util.transfer
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
data class VItemMeta(
    @JsonNames("mat")
    override var material: ItemMaterial = ItemMaterial.EMPTY,
    override var display: ItemDisplay = VItemDisplay(),
    override var durability: ItemDurability = VItemDurability(),
    override var sundry: ItemSundry = VItemSundry()
) : ItemMetadata {

    override fun getNode() = OccupyNode.APEX_NODE

    override fun transform(source: ItemData) {
        source.material = this.material
        this.display.transfer(source)
        this.durability.transfer(source)
        this.sundry.transfer(source)
    }

    override fun detransform(target: ItemData) {
        this.material = target.material
        this.display.accept(target)
        this.durability.accept(target)
        this.sundry.accept(target)
    }

}