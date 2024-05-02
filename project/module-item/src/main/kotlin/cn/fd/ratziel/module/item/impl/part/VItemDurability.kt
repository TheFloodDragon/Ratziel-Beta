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
    @JsonNames("maxDamage", "max-damage", "max-durability", "durability")
    override var maxDurability: Int? = null,
    @JsonNames("repair-cost")
    override var repairCost: Int? = null,
    @JsonNames("isUnbreakable")
    override var unbreakable: Boolean? = null
) : ItemDurability {

    override fun getNode() = OccupyNode.APEX_NODE

    override fun transform(source: ItemData) {
        source.nbt.addAll(
            ItemSheet.UNBREAKABLE to this.unbreakable?.let { NBTByte(it) },
            ItemSheet.REPAIR_COST to this.repairCost?.let { NBTInt(it) },
            ItemSheet.MAX_DAMAGE to this.maxDurability?.let { NBTInt(it) }
        )
    }

    override fun detransform(target: ItemData) {
        val unbreakable = target.nbt[ItemSheet.UNBREAKABLE] as? NBTByte
        if (unbreakable != null) this.unbreakable = unbreakable.contentBoolean
        val repairCost = target.nbt[ItemSheet.REPAIR_COST] as? NBTInt
        if (repairCost != null) this.repairCost = repairCost.content
        val maxDamage = target.nbt[ItemSheet.MAX_DAMAGE] as? NBTInt
        if (maxDamage != null) this.maxDurability = maxDamage.content
    }

}