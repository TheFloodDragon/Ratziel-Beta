package cn.fd.ratziel.module.item.impl.part

import cn.fd.ratziel.module.item.api.common.SimpleDataTransformer
import cn.fd.ratziel.module.item.api.part.ItemDisplay
import cn.fd.ratziel.module.item.api.part.ItemMetadata
import kotlinx.serialization.Serializable

/**
 * VItemMeta
 *
 * @author TheFloodDragon
 * @since 2024/4/4 20:10
 */
@Serializable
data class VItemMeta(
    override var display: ItemDisplay? = null
) : ItemMetadata {

    override fun transformer(): SimpleDataTransformer<ItemMetadata> {
        TODO("Not yet implemented")
    }

}