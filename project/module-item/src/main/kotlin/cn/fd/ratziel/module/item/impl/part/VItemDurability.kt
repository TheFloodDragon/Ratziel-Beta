@file:OptIn(ExperimentalSerializationApi::class)

package cn.fd.ratziel.module.item.impl.part

import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.common.OccupyNode
import cn.fd.ratziel.module.item.api.part.ItemDurability
import cn.fd.ratziel.module.item.nbt.NBTByte
import cn.fd.ratziel.module.item.nbt.NBTInt
import cn.fd.ratziel.module.item.reflex.ItemSheet
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

/**
 * VItemDurability
 *
 * @author TheFloodDragon
 * @since 2024/4/13 16:05
 */
@Serializable
data class VItemDurability(
    @JsonNames("max-durability", "durability-max", "durability")
    override var maxDurability: Int? = null,
    @JsonNames("repair-cost")
    override var repairCost: Int? = null,
    @JsonNames("isUnbreakable", "unbreak")
    override var unbreakable: Boolean? = null
) : ItemDurability {

    override fun getNode() = OccupyNode.APEX_NODE

    // TODO 未完成
    override fun transform(source: ItemData) = source.apply {
        nbt.addAll(
            ItemSheet.UNBREAKABLE to unbreakable?.let { NBTByte(NBTByte.new(it)) },
            ItemSheet.REPAIR_COST to repairCost?.let { NBTInt(NBTInt.new(it)) },
        )
    }

    override fun detransform(target: ItemData) {
        (target.nbt[ItemSheet.UNBREAKABLE] as? NBTByte)?.let { unbreakable = it.contentBoolean }
        (target.nbt[ItemSheet.REPAIR_COST] as? NBTInt)?.let { repairCost = it.content }
    }

}