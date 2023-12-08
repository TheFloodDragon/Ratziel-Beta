@file:OptIn(ExperimentalSerializationApi::class)

package cn.fd.ratziel.module.itemengine.item.meta

import cn.fd.ratziel.module.itemengine.api.attribute.ItemAttribute
import cn.fd.ratziel.module.itemengine.api.builder.ItemTagBuilder
import cn.fd.ratziel.module.itemengine.api.part.meta.ItemCharacteristic
import cn.fd.ratziel.module.itemengine.nbt.NBTCompound
import cn.fd.ratziel.module.itemengine.nbt.NBTTag
import cn.fd.ratziel.module.itemengine.nbt.toTiNBT
import cn.fd.ratziel.module.itemengine.util.mapping.ItemMapping
import cn.fd.ratziel.module.itemengine.util.mapping.RefItemMeta
import com.google.common.collect.LinkedHashMultimap
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
import taboolib.library.reflex.Reflex.Companion.setProperty
import taboolib.module.nms.ItemTag
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
    @JsonNames("attribute", "attributes", "modifier", "modifiers")
    override var attributeModifiers: MutableMap<@Contextual Attribute, MutableList<@Contextual AttributeModifier>>? = null,
) : ItemCharacteristic, ItemAttribute<VItemCharacteristic>(NBTTag.APEX_NODE_SIGN), ItemTagBuilder {

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
        if (enchants == null) enchants = LinkedHashMap() // Null Check
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
     * Bukkit.ItemMeta(BI) -> VItemCharacteristic
     * @param replace 如果BI的元数据存在(空),是否替换
     */
    @Deprecated("考虑转移到别的地方")
    fun applyForm(
        meta: ItemMeta, replace: Boolean = false,
        /**
         * 值的设置
         * 使用变量的形式是为了自定义性
         */
        fCustomModelData: Consumer<VItemCharacteristic> = Consumer {
            if (it.customModelData == null || replace)
                it.customModelData = meta.getProperty<Int>("customModelData")
        },
        fEnchants: Consumer<VItemCharacteristic> = Consumer {
            if (it.enchants?.isEmpty() == true || replace)
                it.enchants = meta.enchants
        },
        fItemFlags: Consumer<VItemCharacteristic> = Consumer {
            if (it.hideFlags?.isEmpty() == true || replace)
                it.hideFlags = meta.itemFlags
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
        fAttributeModifiers.accept(this)
    }

    // TODO 给这坨优化了
    override fun build(tag: NBTTag) {
        // 创建一个空的 ItemMeta
        val meta = RefItemMeta.new() as ItemMeta
        // 创建一个空的 NBTCompound (nms)
        val nbt = NBTCompound.new()
        // 应用到 ItemMeta
        applyTo(meta)
        // 应用到 NBTCompound (nms)
        RefItemMeta.applyToItem(meta, nbt)
        // 构建物品标签
        (toTiNBT(nbt) as ItemTag).forEach { key, value ->
            tag[key] = value
        }
    }

    override fun transform(source: NBTTag) = source.also { tag ->
        // 属性修饰符处理
        NBTTag.new().also { nmsTag ->
            val modifierMap = LinkedHashMultimap.create<Attribute, AttributeModifier>().also { map ->
                attributeModifiers?.forEach { map.putAll(it.key, it.value) }
            }
            RefItemMeta.applyModifiers(nmsTag, modifierMap)
        }.let {
            tag.merge(NBTTag(NBTTag.new()).apply {
                put(ItemMapping.ATTRIBUTE_MODIFIERS.get(), it)
            })
        }
    }

    override fun detransform(input: NBTTag) {
        TODO("Not yet implemented")
    }

    /**
     * 直接转换
     * ItemCharacteristic -> Bukkit.ItemMeta
     */
    fun applyTo(target: ItemMeta) {
        // 自定义模型数据
        if (MinecraftVersion.isHigherOrEqual(MinecraftVersion.V1_14))
            target.setProperty("customModelData", this.customModelData)
        // 附魔
        this.enchants?.forEach { (key, value) ->
            target.addEnchant(key, value, true)
        }
        // 物品隐藏标签
        target.addItemFlags(*this.hideFlags?.toTypedArray() ?: emptyArray())
        // 属性修饰符
        this.attributeModifiers?.forEach { (key, value) ->
            value.forEach { target.addAttributeModifier(key, it) }
        }
    }

}