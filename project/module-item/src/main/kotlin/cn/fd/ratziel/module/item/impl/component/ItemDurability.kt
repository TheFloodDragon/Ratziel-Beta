@file:OptIn(ExperimentalSerializationApi::class)

package cn.fd.ratziel.module.item.impl.component

import cn.fd.ratziel.core.util.putNonNull
import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.ItemTransformer
import cn.fd.ratziel.module.item.impl.OccupyNode
import cn.fd.ratziel.module.item.impl.ItemDataImpl
import cn.fd.ratziel.module.item.nbt.NBTByte
import cn.fd.ratziel.module.item.nbt.NBTCompound
import cn.fd.ratziel.module.item.nbt.NBTInt
import cn.fd.ratziel.module.item.nms.ItemSheet
import cn.fd.ratziel.module.item.util.castThen
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames
import taboolib.module.nms.MinecraftVersion

/**
 * ItemDurability
 *
 * @author TheFloodDragon
 * @since 2024/4/13 16:05
 */
@Serializable
data class ItemDurability(
    @JsonNames("maxDamage", "max-damage", "max-durability", "durability")
    var maxDurability: Int? = null,
    @JsonNames("repair-cost")
    var repairCost: Int? = null,
    @JsonNames("isUnbreakable")
    var unbreakable: Boolean? = null
) {

    companion object : ItemTransformer<ItemDurability> {

        override val node = OccupyNode.APEX_NODE

        override fun detransform(data: ItemData): ItemDurability = ItemDurability().apply {
            data.castThen<NBTInt>(ItemSheet.REPAIR_COST) { this.repairCost = it.content }
            data.castThen<NBTInt>(ItemSheet.MAX_DAMAGE) { this.maxDurability = it.content }
            // 无法破坏部分的特殊处理
            val unsure = data.tag[ItemSheet.UNBREAKABLE]
            if (MinecraftVersion.majorLegacy >= 12005) this.unbreakable = unsure != null
            else if (unsure != null) this.unbreakable = NBTByte.adapt((unsure as NBTByte).content)
        }

        override fun transform(component: ItemDurability): ItemData = ItemDataImpl().apply {
            tag.putNonNull(ItemSheet.REPAIR_COST, component.repairCost?.let { NBTInt(it) })
            tag.putNonNull(ItemSheet.MAX_DAMAGE, component.maxDurability?.let { NBTInt(it) })
            // 无法破坏部分的特殊处理
            if (MinecraftVersion.majorLegacy >= 12005) {
                if (component.unbreakable == true && tag[ItemSheet.UNBREAKABLE] == null) {
                    tag.put(ItemSheet.UNBREAKABLE, NBTCompound())
                } else {
                    tag.remove(ItemSheet.UNBREAKABLE)
                }
            } else tag[ItemSheet.UNBREAKABLE] = component.unbreakable?.let { NBTByte(it) }
        }

    }

}