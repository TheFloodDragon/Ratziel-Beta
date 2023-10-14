package cn.fd.ratziel.item.meta

import cn.fd.ratziel.item.api.ItemCharacteristic
import cn.fd.ratziel.item.api.ItemMetadata
import kotlinx.serialization.Serializable

/**
 * VItemMeta
 *
 * @author TheFloodDragon
 * @since 2023/10/14 16:15
 */
@Serializable
class VItemMeta(
    override val display: VItemDisplay?, override val characteristic: ItemCharacteristic?
) : ItemMetadata {

}