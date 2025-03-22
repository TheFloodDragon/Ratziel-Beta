@file:OptIn(ExperimentalSerializationApi::class)
@file:Suppress("DEPRECATION", "SpellCheckingInspection")

package cn.fd.ratziel.module.item.impl.component

import cn.fd.ratziel.module.item.impl.serialization.serializers.AttributeModifierSerializer
import cn.fd.ratziel.module.item.impl.serialization.serializers.AttributeSerializer
import cn.fd.ratziel.module.item.impl.serialization.serializers.HideFlagSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames
import org.bukkit.inventory.EquipmentSlot

typealias HideFlag = @Serializable(HideFlagSerializer::class) org.bukkit.inventory.ItemFlag
typealias Attribute = @Serializable(AttributeSerializer::class) org.bukkit.attribute.Attribute
typealias AttributeModifier = @Serializable(AttributeModifierSerializer::class) org.bukkit.attribute.AttributeModifier

/**
 * ItemSundry - 物品杂项
 *
 * @author TheFloodDragon
 * @since 2024/5/3 21:06
 */
@Serializable
data class ItemSundry(
    /**
     * 物品自定义模型数据 (1.14+)
     */
    @JsonNames("custom-model-data")
    var customModelData: Int? = null,
    /**
     * 物品隐藏标签 (1.20.5- 但仍能通过BukkitAPI使用)
     */
    @JsonNames("hideflag", "hideflags", "hideFlag")
    var hideFlags: MutableSet<HideFlag>? = null,
    /**
     * 物品属性修饰符
     */
    @JsonNames("attribute-modifiers", "attributeModifiers", "bukkit-attributes")
    var bukkitAttributes: MutableMap<Attribute, MutableList<AttributeModifier>>? = null
) {

    /**
     * 添加物品隐藏标签
     */
    fun addHideFlags(vararg flags: HideFlag) = (hideFlags ?: HashSet<HideFlag>().also { hideFlags = it }).addAll(flags)

    /**
     * 删除物品隐藏标签
     */
    fun removeHideFlags(vararg flags: HideFlag) = hideFlags?.removeAll(flags.toSet())

    /**
     * 获取属性修饰符
     */
    fun getAttributeModifier(attribute: Attribute) = bukkitAttributes?.get(attribute)

    /**
     * 添加属性修饰符
     */
    fun addAttributeModifiers(attribute: Attribute, vararg modifiers: AttributeModifier) =
        (bukkitAttributes ?: HashMap<Attribute, MutableList<AttributeModifier>>().also { bukkitAttributes = it })
            .computeIfAbsent(attribute) { mutableListOf() }.addAll(modifiers)

    /**
     * 删除属性修饰符
     */
    fun removeAttributeModifiers(attribute: Attribute, vararg modifiers: AttributeModifier) = bukkitAttributes?.get(attribute)?.removeAll(modifiers.toSet())

    fun removeAttributeModifiers(slot: EquipmentSlot) =
        bukkitAttributes?.forEach { (key, value) -> value.forEach { if (it.slot == slot) bukkitAttributes?.get(key)?.remove(it) } }

//    companion object : ItemTransformer<ItemSundry> {
//
//        override fun transform(data: ItemData, component: ItemSundry) {
//            val itemMeta = RefItemMeta.of(RefItemMeta.META_ITEM)
//            // HideFlags
//            val flags = component.hideFlags?.toTypedArray()
//            if (flags != null) itemMeta.handle.addItemFlags(*flags)
//            // BukkitAttributes
//            component.bukkitAttributes?.forEach { (key, value) ->
//                value.forEach { itemMeta.handle.addAttributeModifier(key, it) }
//            }
//            // Merge
//            data.tag = itemMeta.applyToTag(data.tag)
//            // CustomModelData (1.14+)
//            if (MinecraftVersion.isHigherOrEqual(MinecraftVersion.V1_14)) {
//                data.write(ItemSheet.CUSTOM_MODEL_DATA, component.customModelData?.let { NbtInt(it) })
//            }
//        }
//
//        override fun detransform(data: ItemData): ItemSundry = ItemSundry().apply {
//            val itemMeta = RefItemMeta.of(RefItemMeta.META_ITEM, data.tag)
//            // HideFlags
//            val hideFlags = itemMeta.handle.itemFlags
//            if (hideFlags.isNotEmpty()) {
//                addHideFlags(*hideFlags.toTypedArray())
//            }
//            // BukkitAttributes
//            val bukkitAttributes = itemMeta.handle.attributeModifiers
//            if (bukkitAttributes != null && !bukkitAttributes.isEmpty) {
//                bukkitAttributes.forEach { key, value -> addAttributeModifiers(key, value) }
//            }
//            // CustomModelData (1.14+)
//            if (MinecraftVersion.isHigherOrEqual(MinecraftVersion.V1_14)) {
//                data.read<NbtInt>(ItemSheet.CUSTOM_MODEL_DATA) {
//                    customModelData = it.content
//                }
//            }
//        }
//
//    }

}