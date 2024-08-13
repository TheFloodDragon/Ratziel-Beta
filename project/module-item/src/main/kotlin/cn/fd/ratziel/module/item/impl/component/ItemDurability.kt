@file:OptIn(ExperimentalSerializationApi::class)

package cn.fd.ratziel.module.item.impl.component

import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.builder.ItemTransformer
import cn.fd.ratziel.module.item.nbt.NBTByte
import cn.fd.ratziel.module.item.nbt.NBTCompound
import cn.fd.ratziel.module.item.nbt.NBTInt
import cn.fd.ratziel.module.item.nms.ItemSheet
import cn.fd.ratziel.module.item.util.read
import cn.fd.ratziel.module.item.util.write
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames
import taboolib.module.nms.MinecraftVersion

/**
 * ItemDurability - 物品耐久
 *
 * @author TheFloodDragon
 * @since 2024/4/13 16:05
 */
@Serializable
data class ItemDurability(
    /**
     * 物品最大耐久
     */
    @JsonNames("maxDamage", "max-damage", "max-durability", "durability")
    var maxDurability: Int? = null,
    /**
     * 物品修复消耗
     */
    @JsonNames("repair-cost")
    var repairCost: Int? = null,
    /**
     * 物品是否无法破坏
     */
    @JsonNames("isUnbreakable")
    var unbreakable: Boolean? = null
) {

    companion object : ItemTransformer<ItemDurability> {

        override fun transform(data: ItemData.Mutable, component: ItemDurability) {
            data.write(ItemSheet.REPAIR_COST, component.repairCost?.let { NBTInt(it) })
            data.write(ItemSheet.MAX_DAMAGE, component.maxDurability?.let { NBTInt(it) })
            // 无法破坏部分的特殊处理
            if (MinecraftVersion.majorLegacy >= 12005) {
                if (component.unbreakable == true && data.tag[ItemSheet.UNBREAKABLE.name] == null) {
                    data.tag.put(ItemSheet.UNBREAKABLE.name, NBTCompound())
                } else {
                    data.tag.remove(ItemSheet.UNBREAKABLE.name)
                }
            } else data.tag[ItemSheet.UNBREAKABLE.name] = component.unbreakable?.let { NBTByte(it) }
        }

        override fun detransform(data: ItemData): ItemDurability = ItemDurability().apply {
            data.read<NBTInt>(ItemSheet.REPAIR_COST) { this.repairCost = it.content }
            data.read<NBTInt>(ItemSheet.MAX_DAMAGE) { this.maxDurability = it.content }
            // 无法破坏部分的特殊处理
            val unsure = data.tag[ItemSheet.UNBREAKABLE.name]
            if (MinecraftVersion.majorLegacy >= 12005) this.unbreakable = unsure != null
            else if (unsure != null) this.unbreakable = NBTByte.adapt((unsure as NBTByte).content)
        }

    }

}