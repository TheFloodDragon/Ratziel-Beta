@file:OptIn(ExperimentalSerializationApi::class)

package cn.fd.ratziel.module.item.impl.component

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

/**
 * ItemDurability - 物品耐久
 *
 * @author TheFloodDragon
 * @since 2024/4/13 16:05
 */
@Serializable
class ItemDurability(
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
)