@file:OptIn(ExperimentalSerializationApi::class)

package cn.fd.ratziel.item.meta

import cn.fd.ratziel.item.meta.serializers.AttributeModifierSerializer
import cn.fd.ratziel.item.meta.serializers.AttributeSerializer
import cn.fd.ratziel.item.meta.serializers.EnchantmentSerializer
import com.google.common.collect.LinkedHashMultimap
import com.google.common.collect.Multimap
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.meta.ItemMeta
import taboolib.library.reflex.Reflex.Companion.setProperty
import taboolib.module.nms.MinecraftVersion


/**
 * XItemMeta
 * Bukkit中ItemMeta的重写
 *
 * @author TheFloodDragon
 * @since 2023/10/2 16:46
 */
@Serializable
class XItemMeta {

    /**
     * 显示名称
     */
    @JsonNames("name", "display-name")
    var displayName: String? = null

    /**
     * 本地化名称
     */
    @JsonNames("loc-name", "local-name")
    var localizedName: String? = null

    /**
     * 物品描述
     */
    @JsonNames("lores")
    var lore: List<String> = emptyList()

    /**
     * 自定义模型数据 (1.14+)
     */
    @JsonNames("custom-model-data", "cmd")
    var customModelData: Int? = null

    /**
     * 魔咒列表
     */
    @JsonNames("enchant", "enchantment", "enchantments")
    val enchants: MutableMap<@Serializable(with = EnchantmentSerializer::class) Enchantment, Int> = mutableMapOf()

    /**
     * 是否含有魔咒
     */
    fun hasEnchant(enchantment: Enchantment): Boolean = enchants.contains(enchantment)

    /**
     * 获取魔咒等级
     */
    fun getEnchantLevel(enchantment: Enchantment): Int? = enchants[enchantment]

    /**
     * 添加魔咒
     * @param level 魔咒等级
     * @param ignoreLevelRestriction 是否忽略魔咒等级限制
     * @return 魔咒等级是否超过限制
     */
    fun addEnchant(enchantment: Enchantment, level: Int, ignoreLevelRestriction: Boolean) {
        if (!ignoreLevelRestriction) {
            level.coerceIn(enchantment.startLevel, enchantment.maxLevel)
        }
        this.enchants[enchantment] = level
    }

    /**
     * 删除魔咒
     */
    fun removeEnchant(enchantment: Enchantment) {
        enchants.remove(enchantment)
    }

    /**
     * 魔咒是否有冲突
     */
    fun hasConflictingEnchant(enchantment: Enchantment): Boolean = enchants.keys.contains(enchantment)

    /**
     * 物品标志
     */
    @JsonNames("flag", "flags", "itemFlag")
    val itemFlags: MutableList<ItemFlag> = mutableListOf()

    /**
     * 添加物品标志
     */
    fun addItemFlags(vararg flags: ItemFlag) {
        flags.forEach { itemFlags.add(it) }
    }

    /**
     * 删除物品标志
     */
    fun removeItemFlags(vararg flags: ItemFlag) {
        flags.forEach { itemFlags.remove(it) }
    }

    /**
     * 物品是否不可破坏
     */
    @JsonNames("isUnbreakable")
    var unbreakable: Boolean = false

    /**
     * 物品属性修饰符
     */
    @JsonNames("attribute", "attributes", "modifier", "modifiers")
    val attributeModifiers: Multimap<
            @Serializable(with = AttributeSerializer::class) Attribute,
            @Serializable(with = AttributeModifierSerializer::class) AttributeModifier> = LinkedHashMultimap.create()

    /**
     * 获取属性修饰符
     */
    fun getAttributeModifiers(slot: EquipmentSlot): Multimap<Attribute, AttributeModifier> =
        LinkedHashMultimap.create<Attribute, AttributeModifier>().apply {
            forEach { key, value ->
                if (value.slot == slot) put(key, value)
            }
        }

    fun getAttributeModifiers(attribute: Attribute): Collection<AttributeModifier> = attributeModifiers[attribute]

    /**
     * 添加属性修饰符
     */
    fun addAttributeModifiers(attribute: Attribute, vararg modifiers: AttributeModifier) {
        attributeModifiers.putAll(attribute, modifiers.toMutableList())
    }

    /**
     * 删除属性修饰符
     */
    fun removeAttributeModifiers(attribute: Attribute) {
        attributeModifiers.removeAll(attribute)
    }

    fun removeAttributeModifiers(slot: EquipmentSlot) {
        attributeModifiers.forEach { key, value ->
            if (value.slot == slot) attributeModifiers.remove(key, value)
        }
    }

    fun removeAttributeModifier(attribute: Attribute, modifier: AttributeModifier) {
        attributeModifiers.remove(attribute, modifier)
    }

    /**
     * 获取Bukkit的ItemMeta
     */
    fun itemMetaBy(meta: ItemMeta) =
        meta.clone().apply {
            setProperty("displayName", displayName)
            setLocalizedName(localizedName)
            setProperty("lore", lore)
            if (MinecraftVersion.isHigherOrEqual(MinecraftVersion.V1_14)) {
                setProperty("customModelData", Integer.valueOf(customModelData))
            }
            this.enchants.clear()
            this@XItemMeta.enchants.forEach { this.addEnchant(it.key, it.value, true) }
            this.itemFlags.clear()
            this.addItemFlags(*this@XItemMeta.itemFlags.toSet().toTypedArray())
            this.isUnbreakable = this@XItemMeta.unbreakable
            this.attributeModifiers = this@XItemMeta.attributeModifiers
        }

}