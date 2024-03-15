@file:OptIn(ExperimentalSerializationApi::class)

package cn.fd.ratziel.module.itemengine.item.meta

import cn.fd.ratziel.module.itemengine.api.attribute.ItemAttribute
import cn.fd.ratziel.module.itemengine.api.attribute.NBTTransformer
import cn.fd.ratziel.module.itemengine.api.part.meta.ItemDurability
import cn.fd.ratziel.module.item.reflex.ItemMapping
import cn.fd.ratziel.module.itemengine.nbt.NBTBoolean
import cn.fd.ratziel.module.itemengine.nbt.NBTByte
import cn.fd.ratziel.module.itemengine.nbt.NBTInt
import cn.fd.ratziel.module.itemengine.nbt.NBTTag
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

/**
 * VItemDurability
 *
 * @author TheFloodDragon
 * @since 2023/10/15 8:52
 */
@Serializable
data class VItemDurability(
    @JsonNames("max-durability", "durability-max", "durability")
    override var maxDurability: Int? = null,
    @JsonNames("current-durability", "durability-current")
    override var currentDurability: Int? = maxDurability,
    @JsonNames("repair-cost")
    override var repairCost: Int? = null,
    @JsonNames("isUnbreakable", "unbreak")
    override var unbreakable: Boolean? = null,
) : ItemDurability, ItemAttribute<VItemDurability> {

    /**
     * 物品损伤值
     *   = 最大耐久-当前耐久
     */
    var damage: Int?
        get() = currentDurability?.let { maxDurability?.minus(it) }
        set(value) {
            value?.let { currentDurability = maxDurability?.minus(it) }
        }

    override val transformer get() = Companion

    companion object : NBTTransformer<VItemDurability> {

        // TODO 需要特殊处理
        override fun transform(target: VItemDurability, source: NBTTag) = target.run {
            source.putAll(
                ItemMapping.DAMAGE.get() to damage?.let { NBTInt(it) },
                ItemMapping.UNBREAKABLE.get() to unbreakable?.let { NBTBoolean(it) },
                ItemMapping.REPAIR_COST.get() to repairCost?.let { NBTInt(it) }
            )
        }

        override fun detransform(target: VItemDurability, from: NBTTag): Unit = target.run {
            from[ItemMapping.DAMAGE.get()]?.let { damage = (it as? NBTInt)?.content }
            // TODO Finish this
            from[ItemMapping.UNBREAKABLE.get()]?.let { unbreakable = (it as? NBTByte)?.toString().toBoolean() }
            from[ItemMapping.REPAIR_COST.get()]?.let { repairCost = (it as? NBTInt)?.content }
        }

    }

}