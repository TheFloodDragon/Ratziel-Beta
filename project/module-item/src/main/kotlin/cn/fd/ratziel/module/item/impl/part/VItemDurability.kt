@file:OptIn(ExperimentalSerializationApi::class)

package cn.fd.ratziel.module.item.impl.part

import cn.fd.ratziel.module.item.api.part.ItemDurability
import cn.fd.ratziel.module.item.api.part.ItemMaterial
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
    @JsonNames("current-durability", "durability-current")
    override var currentDurability: Int?,
    @JsonNames("max-durability", "durability-max", "durability")
    override var maxDurability: Int?,
    @JsonNames("repair-cost")
    override var repairCost: Int?,
    @JsonNames("isUnbreakable", "unbreak")
    override var unbreakable: Boolean?
) : ItemDurability {

    override fun getMaxDurability(material: ItemMaterial): Int = this.maxDurability ?: material.maxDurability

    override fun getDamage(): Int? {
        TODO("Not yet implemented")
    }

    override fun getDamage(material: ItemMaterial): Int {
        TODO("Not yet implemented")
    }

    override fun setDamage(value: Int) {
        TODO("Not yet implemented")
    }

    override fun setDamage(value: Int, material: ItemMaterial) {
        TODO("Not yet implemented")
    }

    override fun transformer() = TODO("Not yet implemented")

}
