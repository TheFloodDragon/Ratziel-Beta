package cn.fd.ratziel.module.item.impl.part

import cn.fd.ratziel.module.item.api.NodeDistributor
import cn.fd.ratziel.module.item.api.part.ItemDisplay
import cn.fd.ratziel.module.item.api.part.ItemDurability
import cn.fd.ratziel.module.item.api.part.ItemMetadata
import cn.fd.ratziel.module.item.nbt.NBTCompound
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

    override fun node(): NodeDistributor {
        TODO("Not yet implemented")
    }

    override fun transform(): NBTCompound {
        TODO("Not yet implemented")
    }

    override fun detransform(from: NBTCompound) {
        TODO("Not yet implemented")
    }

}