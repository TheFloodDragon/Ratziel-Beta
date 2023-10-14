package cn.fd.ratziel.item.meta

import cn.fd.ratziel.item.api.ItemDisplay
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
    /**
     * TODO 并行序列化
     *
     * 并行序列化: 不用 display:{name:"",lore:""}
     *           用 [name:"",lore:""]
     */
    override val display: ItemDisplay?
) : ItemMetadata{

}