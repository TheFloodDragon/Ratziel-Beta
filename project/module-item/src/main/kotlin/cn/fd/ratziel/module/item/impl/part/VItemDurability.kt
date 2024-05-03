@file:OptIn(ExperimentalSerializationApi::class)

package cn.fd.ratziel.module.item.impl.part

import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.common.OccupyNode
import cn.fd.ratziel.module.item.api.part.ItemDurability
import cn.fd.ratziel.module.item.nbt.NBTByte
import cn.fd.ratziel.module.item.nbt.NBTCompound
import cn.fd.ratziel.module.item.nbt.NBTInt
import cn.fd.ratziel.module.item.nbt.addAll
import cn.fd.ratziel.module.item.reflex.ItemSheet
import cn.fd.ratziel.module.item.util.castThen
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
        source.nbt.addAll(
            ItemSheet.REPAIR_COST to this.repairCost?.let { NBTInt(it) },
            ItemSheet.MAX_DAMAGE to this.maxDurability?.let { NBTInt(it) }
        )
        // 无法破坏部分的特殊处理
        if (MinecraftVersion.majorLegacy >= 12005) {
            if (unbreakable == true && source.nbt[ItemSheet.UNBREAKABLE] == null) {
                source.nbt.put(ItemSheet.UNBREAKABLE, NBTCompound())
            } else {
                source.nbt.remove(ItemSheet.UNBREAKABLE)
            }
        } else source.nbt[ItemSheet.UNBREAKABLE] = unbreakable?.let { NBTByte(it) }
    }

    override fun detransform(target: ItemData) {
        target.nbt[ItemSheet.REPAIR_COST].castThen<NBTInt> {
            this.repairCost = it.content
        }
        target.nbt[ItemSheet.MAX_DAMAGE].castThen<NBTInt> {
            this.maxDurability = it.content
        }
        // 无法破坏部分的特殊处理
        val unsure = target.nbt[ItemSheet.UNBREAKABLE]
        if (MinecraftVersion.majorLegacy >= 12005) this.unbreakable = unsure != null
        else if (unsure != null) this.unbreakable = NBTByte.adapt((unsure as NBTByte).content)
    }

}