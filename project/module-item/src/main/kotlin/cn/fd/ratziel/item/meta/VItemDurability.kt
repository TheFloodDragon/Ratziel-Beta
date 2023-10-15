@file:OptIn(ExperimentalSerializationApi::class)

package cn.fd.ratziel.item.meta

import cn.fd.ratziel.item.api.ItemDurability
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
    @JsonNames("max-durability", "durability-max", "max")
    override var maxDurability: Int? = null,
    @JsonNames("current-durability", "durability-current", "durability", "current")
    override var currentDurability: Int? = null,
    @JsonNames("repair-cost")
    override var repairCost: Int? = null,
) : ItemDurability