@file:OptIn(ExperimentalSerializationApi::class)

package cn.fd.ratziel.module.item.impl.part

import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.common.OccupyNode
import cn.fd.ratziel.module.item.api.part.ItemDurability
import cn.fd.ratziel.module.item.nbt.NBTByte
import cn.fd.ratziel.module.item.nbt.NBTCompound
import cn.fd.ratziel.module.item.nbt.NBTData
import cn.fd.ratziel.module.item.nbt.NBTInt
import cn.fd.ratziel.module.item.reflex.ItemSheet
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames
import taboolib.module.nms.MinecraftVersion

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
        fun handle0(unbreakable: Boolean?): NBTData? =
            if (MinecraftVersion.majorLegacy >= 12005) {
                if (unbreakable == true) NBTCompound() else null
            } else unbreakable?.let { NBTByte(it) }
        source.nbt.addAll(
            ItemSheet.UNBREAKABLE to handle0(this.unbreakable),
            ItemSheet.REPAIR_COST to this.repairCost?.let { NBTInt(it) },
            ItemSheet.MAX_DAMAGE to this.maxDurability?.let { NBTInt(it) }
        )
    }

    override fun detransform(target: ItemData) {
        val unbreakable = target.nbt[ItemSheet.UNBREAKABLE] as? NBTByte
        if (unbreakable != null) this.unbreakable = NBTByte.adapt(unbreakable.content)
        val repairCost = target.nbt[ItemSheet.REPAIR_COST] as? NBTInt
        if (repairCost != null) this.repairCost = repairCost.content
        val maxDamage = target.nbt[ItemSheet.MAX_DAMAGE] as? NBTInt
        if (maxDamage != null) this.maxDurability = maxDamage.content
    }

}