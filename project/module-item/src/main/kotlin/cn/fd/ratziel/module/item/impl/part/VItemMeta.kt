package cn.fd.ratziel.module.item.impl.part

import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.common.OccupyNode
import cn.fd.ratziel.module.item.api.part.ItemDisplay
import cn.fd.ratziel.module.item.api.part.ItemDurability
import cn.fd.ratziel.module.item.api.part.ItemMetadata
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
    override var durability: ItemDurability? = null
) : ItemMetadata {

    override fun getNode() = OccupyNode.APEX_NODE
    override fun transform(source: ItemData) = source.apply {
        arrayOf(display, durability).forEach { it?.applyTo(nbt) }
    }

    override fun detransform(target: ItemData) {
        TODO("Not yet implemented")
    }

}