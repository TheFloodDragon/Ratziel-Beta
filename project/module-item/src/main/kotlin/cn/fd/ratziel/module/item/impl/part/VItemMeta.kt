package cn.fd.ratziel.module.item.impl.part

import cn.fd.ratziel.module.item.api.ItemComponent
import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.common.OccupyNode
import cn.fd.ratziel.module.item.api.part.ItemDisplay
import cn.fd.ratziel.module.item.api.part.ItemDurability
import cn.fd.ratziel.module.item.api.part.ItemMetadata
import cn.fd.ratziel.module.item.api.part.ItemSundry
import cn.fd.ratziel.module.item.util.applyTo
import kotlinx.serialization.Serializable

/**
 * VItemMeta
 *
 * @author TheFloodDragon
 * @since 2024/4/4 20:10
 */
@Serializable
data class VItemMeta(
    override var display: ItemDisplay? = null,
    override var durability: ItemDurability? = null,
    override var sundry: ItemSundry? = null
) : ItemMetadata {

    override fun getNode() = OccupyNode.APEX_NODE

    override fun transform(source: ItemData) = elements.forEach { it?.applyTo(source) }

    override fun detransform(target: ItemData) = elements.forEach { it?.detransform(target) }

    private val elements: Array<ItemComponent?> get() = arrayOf(display, durability, sundry)

}