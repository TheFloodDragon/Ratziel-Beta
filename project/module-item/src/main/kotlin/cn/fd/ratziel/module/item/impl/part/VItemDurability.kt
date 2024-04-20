@file:OptIn(ExperimentalSerializationApi::class)

package cn.fd.ratziel.module.item.impl.part

import cn.fd.ratziel.module.item.api.common.OccupyNode
import cn.fd.ratziel.module.item.api.part.ItemDurability
import cn.fd.ratziel.module.item.nbt.NBTByte
import cn.fd.ratziel.module.item.nbt.NBTCompound
import cn.fd.ratziel.module.item.nbt.NBTInt
import cn.fd.ratziel.module.item.reflex.ItemMapping
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

    override fun node() = OccupyNode.APEX_NODE

    override fun transform() = NBTCompound(NBTCompound.new()).putAll(
        TODO("MAX"),
        ItemMapping.UNBREAKABLE.mapping to unbreakable?.let { NBTByte(NBTByte.new(it)) },
        ItemMapping.REPAIR_COST.mapping to repairCost?.let { NBTInt(NBTInt.new(it)) },
    )

    override fun detransform(from: NBTCompound) {
        TODO("MAX")
        (from[ItemMapping.UNBREAKABLE.mapping] as? NBTByte)?.let { unbreakable = it.contentBoolean }
        (from[ItemMapping.REPAIR_COST.mapping] as? NBTInt)?.let { repairCost = it.content }
    }

}