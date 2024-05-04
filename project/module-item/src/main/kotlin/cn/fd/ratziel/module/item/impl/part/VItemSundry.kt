@file:OptIn(ExperimentalSerializationApi::class)
@file:Suppress( "DEPRECATION")

package cn.fd.ratziel.module.item.impl.part

import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.common.OccupyNode
import cn.fd.ratziel.module.item.api.part.HideFlag
import cn.fd.ratziel.module.item.api.part.ItemSundry
import cn.fd.ratziel.module.item.nbt.NBTInt
import cn.fd.ratziel.module.item.reflex.ItemSheet
import cn.fd.ratziel.module.item.reflex.RefItemMeta
import cn.fd.ratziel.module.item.util.castThen
import kotlinx.serialization.Contextual
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.inventory.EquipmentSlot
import taboolib.module.nms.MinecraftVersion

/**
 * VItemSundry
 *
 * @author TheFloodDragon
 * @since 2024/5/3 21:06
 */
@Serializable
data class VItemSundry(
    @JsonNames("custom-model-data")
    override var customModelData: Int? = null,
    @JsonNames("hideflag", "hideflags", "hideFlag")
    override var hideFlags: MutableSet<@Contextual HideFlag>? = null,
    @JsonNames("attribute-modifiers", "attributeModifiers", "bukkit-attributes")
    override var bukkitAttributes: MutableMap<@Contextual Attribute, MutableList<@Contextual AttributeModifier>>? = null
) : ItemSundry {

    /**
     * 添加物品隐藏标签
     */
    fun addHideFlags(vararg flags: HideFlag) = fetchHideFlags().addAll(flags)

    /**
     * 删除物品隐藏标签
     */
    fun removeHideFlags(vararg flags: HideFlag) = fetchHideFlags().removeAll(flags.toSet())

    /**
     * 获取属性修饰符
     */
    fun getAttributeModifier(attribute: Attribute) = fetchBukkitAttributes().computeIfAbsent(attribute) { mutableListOf() }

    /**
     * 添加属性修饰符
     */
    fun addAttributeModifiers(attribute: Attribute, vararg modifiers: AttributeModifier) = getAttributeModifier(attribute).addAll(modifiers)

    /**
     * 删除属性修饰符
     */
    fun removeAttributeModifiers(attribute: Attribute, vararg modifiers: AttributeModifier) = bukkitAttributes?.get(attribute)?.removeAll(modifiers.toSet())

    fun removeAttributeModifiers(slot: EquipmentSlot) =
        bukkitAttributes?.forEach { (key, value) -> value.forEach { if (it.slot == slot) bukkitAttributes?.get(key)?.remove(it) } }

    override fun getNode() = OccupyNode.APEX_NODE

    override fun transform(source: ItemData) {
        val itemMeta = RefItemMeta()
        // HideFlags
        itemMeta.handle.addItemFlags(*fetchHideFlags().toTypedArray())
        // BukkitAttributes
        fetchBukkitAttributes().forEach { (key, value) ->
            value.forEach { itemMeta.handle.addAttributeModifier(key, it) }
        }
        // Merge
        itemMeta.applyToTag(source.nbt)
        // CustomModelData (1.14+)
        if (MinecraftVersion.isHigherOrEqual(MinecraftVersion.V1_14)) {
            source.nbt[ItemSheet.CUSTOM_MODEL_DATA] = customModelData?.let { NBTInt(it) }
        }
    }

    override fun detransform(target: ItemData) {
        val itemMeta = RefItemMeta(target.nbt)
        // HideFlags
        val hideFlags = itemMeta.handle.itemFlags
        if (hideFlags.isNotEmpty()) {
            addHideFlags(*hideFlags.toTypedArray())
        }
        // BukkitAttributes
        val bukkitAttributes = itemMeta.handle.attributeModifiers
        if (bukkitAttributes != null && !bukkitAttributes.isEmpty) {
            bukkitAttributes.forEach { key, value -> addAttributeModifiers(key, value) }
        }
        // CustomModelData (1.14+)
        if (MinecraftVersion.isHigherOrEqual(MinecraftVersion.V1_14)) {
            target.nbt[ItemSheet.CUSTOM_MODEL_DATA].castThen<NBTInt> {
                customModelData = it.content
            }
        }
    }

    /**
     * 空->默认值处理
     */

    private fun fetchHideFlags() = hideFlags ?: HashSet<HideFlag>().also { hideFlags = it }

    private fun fetchBukkitAttributes() = bukkitAttributes ?: HashMap<Attribute, MutableList<AttributeModifier>>().also { bukkitAttributes = it }

}