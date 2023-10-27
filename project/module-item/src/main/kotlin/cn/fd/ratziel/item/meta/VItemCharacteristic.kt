@file:OptIn(ExperimentalSerializationApi::class)

package cn.fd.ratziel.item.meta

import cn.fd.ratziel.item.api.meta.ItemCharacteristic
import cn.fd.ratziel.item.api.meta.ItemMetaBuilder
import kotlinx.serialization.Contextual
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.meta.ItemMeta
import taboolib.library.reflex.Reflex.Companion.getProperty
import taboolib.module.nms.MinecraftVersion
import java.util.function.Consumer

/**
 * VItemCharacteristic
 *
 * @author TheFloodDragon
 * @since 2023/10/14 18:38
 */
@Serializable
data class VItemCharacteristic(
    @JsonNames("custom-model-data", "cmd")
    override var customModelData: Int? = null,
    @JsonNames("enchant", "enchantment", "enchantments")
    override var enchants: MutableMap<@Contextual Enchantment, Int>? = null,
    @JsonNames("hideflag", "hideflags", "hideFlag")
    override var hideFlags: MutableSet<@Contextual ItemFlag>? = null,
    @JsonNames("isUnbreakable", "unbreak")
    override var unbreakable: Boolean? = null,
    @JsonNames("attribute", "attributes", "modifier", "modifiers")
    override var attributeModifiers: MutableMap<@Contextual Attribute, MutableList<@Contextual AttributeModifier>>? = null,
) : ItemCharacteristic, ItemMetaBuilder {

    /**
     * 是否含有魔咒
     */
    fun hasEnchant(enchantment: Enchantment): Boolean = enchants?.contains(enchantment) ?: false

    /**
     * 获取魔咒等级
     */
    fun getEnchantLevel(enchantment: Enchantment): Int? = enchants?.get(enchantment)

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
        if (enchants == null) enchants = mutableMapOf() // Null Check
        this.enchants!![enchantment] = level
    }

    /**
     * 删除魔咒
     */
    fun removeEnchant(enchantment: Enchantment) {
        enchants?.remove(enchantment)
    }

    /**
     * 魔咒是否有冲突
     */
    fun hasConflictingEnchant(enchantment: Enchantment): Boolean = enchants?.keys?.contains(enchantment) ?: false

    /**
     * 添加物品标志
     */
    fun addItemFlags(vararg flags: ItemFlag) {
        flags.forEach { hideFlags?.add(it) }
    }

    /**
     * 删除物品标志
     */
    fun removeItemFlags(vararg flags: ItemFlag) {
        flags.forEach { hideFlags?.remove(it) }
    }

    /**
     * 获取属性修饰符
     */
    fun getAttributeModifiers(attribute: Attribute): MutableList<AttributeModifier> {
        if (attributeModifiers == null) attributeModifiers = mutableMapOf() // Null Check
        return attributeModifiers!!.computeIfAbsent(attribute) { mutableListOf() }
    }

    /**
     * 添加属性修饰符
     */
    fun addAttributeModifiers(attribute: Attribute, vararg modifiers: AttributeModifier) {
        getAttributeModifiers(attribute).addAll(modifiers)
    }

    /**
     * 删除属性修饰符
     */
    fun removeAttributeModifiers(attribute: Attribute) {
        attributeModifiers?.set(attribute, mutableListOf())
    }

    fun removeAttributeModifiers(slot: EquipmentSlot) {
        attributeModifiers?.forEach { (key, value) ->
            value.forEach {
                if (it.slot == slot) attributeModifiers?.get(key)?.remove(it)
            }
        }
    }

    fun removeAttributeModifier(attribute: Attribute, modifier: AttributeModifier) {
        attributeModifiers?.get(attribute)?.remove(modifier)
    }

    /**
     * Bukkit.ItemMeta -> VItemCharacteristic
     * @param replace 如果元数据存在,是否替换 (默认true)
     */
    fun applyForm(
        meta: ItemMeta, replace: Boolean = true,
        /**
         * 值的设置
         * 使用变量的形式是为了自定义性
         */
        fCustomModelData: Consumer<VItemCharacteristic> = Consumer {
            if (it.customModelData == null || replace)
                it.customModelData = getProperty<Int>("customModelData")
        },
        fEnchants: Consumer<VItemCharacteristic> = Consumer {
            if (it.enchants?.isEmpty() == true || replace)
                it.enchants = meta.enchants
        },
        fItemFlags: Consumer<VItemCharacteristic> = Consumer {
            if (it.hideFlags?.isEmpty() == true || replace)
                it.hideFlags = meta.itemFlags
        },
        fUnbreakable: Consumer<VItemCharacteristic> = Consumer {
            it.unbreakable = meta.isUnbreakable
        },
        fAttributeModifiers: Consumer<VItemCharacteristic> = Consumer {
            if (it.attributeModifiers?.isEmpty() == true || replace)
                it.attributeModifiers.apply {
                    meta.attributeModifiers?.forEach { key, value ->
                        addAttributeModifiers(key, value)
                    }
                }
        },
    ) {
        if (MinecraftVersion.isHigherOrEqual(MinecraftVersion.V1_14))
            fCustomModelData.accept(this)
        fEnchants.accept(this)
        fItemFlags.accept(this)
        fUnbreakable.accept(this)
        fAttributeModifiers.accept(this)
    }

    /**
     * VItemCharacteristic -> Bukkit.ItemMeta (空的)
     * @param clone 是否对 Bukkit.ItemMeta 进行克隆 (默认true)
     */
    fun applyTo(
        source: ItemMeta, clone: Boolean = true,
        /**
         * 值的设置
         * 使用变量的形式是为了自定义性
         */
        fCustomModelData: Consumer<ItemMeta> = Consumer {
            it.setCustomModelData(customModelData)
        },
        fEnchants: Consumer<ItemMeta> = Consumer {
            enchants?.forEach { (key, value) ->
                it.addEnchant(key, value, true)
            }
        },
        fItemFlags: Consumer<ItemMeta> = Consumer {
            it.addItemFlags(*hideFlags?.toTypedArray() ?: emptyArray())
        },
        fUnbreakable: Consumer<ItemMeta> = Consumer {
            it.isUnbreakable = unbreakable == true
        },
        fAttributeModifiers: Consumer<ItemMeta> = Consumer {
            this@VItemCharacteristic.attributeModifiers?.forEach { (key, values) ->
                values.forEach { v ->
                    it.addAttributeModifier(key, v)
                }
            }
        },
    ) = (if (clone) source.clone() else source).apply {
        if (MinecraftVersion.isHigherOrEqual(MinecraftVersion.V1_14))
            fCustomModelData.accept(this)
        fEnchants.accept(this)
        fItemFlags.accept(this)
        fUnbreakable.accept(this)
        fAttributeModifiers.accept(this)
    }

    override fun build(meta: ItemMeta) {
        applyTo(meta, false)
    }

}