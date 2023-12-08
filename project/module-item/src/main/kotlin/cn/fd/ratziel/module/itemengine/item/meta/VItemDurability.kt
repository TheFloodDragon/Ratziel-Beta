@file:OptIn(ExperimentalSerializationApi::class)

package cn.fd.ratziel.module.itemengine.item.meta

import cn.fd.ratziel.module.itemengine.api.part.meta.ItemDurability
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
    override val unbreakable: Boolean? = null,
) : ItemDurability {

    /**
     * 物品损伤值
     *   = 最大耐久-当前耐久
     */
    val damage: Int?
        get() = currentDurability?.let { maxDurability?.minus(it) }

}