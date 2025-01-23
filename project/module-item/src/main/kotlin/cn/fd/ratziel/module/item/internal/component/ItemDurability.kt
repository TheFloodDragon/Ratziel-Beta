@file:OptIn(ExperimentalSerializationApi::class)

package cn.fd.ratziel.module.item.internal.component

import cn.altawk.nbt.tag.NbtByte
import cn.altawk.nbt.tag.NbtCompound
import cn.altawk.nbt.tag.NbtInt
import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.builder.ItemTransformer
import cn.fd.ratziel.module.item.internal.nms.ItemSheet
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

        override fun transform(data: ItemData, component: ItemDurability) {
            data.write(ItemSheet.REPAIR_COST, component.repairCost?.let { NbtInt(it) })
            data.write(ItemSheet.MAX_DAMAGE, component.maxDurability?.let { NbtInt(it) })
            // 无法破坏部分的特殊处理
            if (MinecraftVersion.versionId >= 12005) {
                if (component.unbreakable == true && data.tag[ItemSheet.UNBREAKABLE.name] == null) {
                    data.tag[ItemSheet.UNBREAKABLE.name] = NbtCompound()
                } else {
                    data.tag.remove(ItemSheet.UNBREAKABLE.name)
                }
            } else component.unbreakable?.let { data.tag[ItemSheet.UNBREAKABLE.name] = NbtByte(it) }
        }

        override fun detransform(data: ItemData): ItemDurability = ItemDurability().apply {
            data.read<NbtInt>(ItemSheet.REPAIR_COST) { this.repairCost = it.content }
            data.read<NbtInt>(ItemSheet.MAX_DAMAGE) { this.maxDurability = it.content }
            // 无法破坏部分的特殊处理
            val unsure = data.tag[ItemSheet.UNBREAKABLE.name]
            if (MinecraftVersion.versionId >= 12005) this.unbreakable = unsure != null
            else if (unsure != null) this.unbreakable = (unsure as NbtByte).toBoolean()
        }

    }

}