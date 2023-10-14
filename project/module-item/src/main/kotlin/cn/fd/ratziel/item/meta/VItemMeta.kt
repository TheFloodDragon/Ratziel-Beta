@file:OptIn(ExperimentalSerializationApi::class)

package cn.fd.ratziel.item.meta

import cn.fd.ratziel.adventure.ComponentSerializer
import cn.fd.ratziel.adventure.jsonToComponent
import kotlinx.serialization.Contextual
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames
import net.kyori.adventure.text.Component
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
 * VItemMeta
 * 对Bukkit.ItemMeta的包装
 * 可以用于Kotlin序列化
 *
 * @author TheFloodDragon
 * @since 2023/10/2 16:46
 */
@Serializable
class VItemMeta {

    /**
     * 显示名称
     */
    @JsonNames("name", "display-name", "displayname")
    @Serializable(with = ComponentSerializer::class)
    var displayName: Component? = null

    /**
     * 本地化名称
     */
    @JsonNames("loc-name", "local-name")
    var localizedName: String? = null

    /**
     * 物品描述
     */
    @JsonNames("lores")
    @Contextual
    var lore: MutableList<Component> = mutableListOf()

    /**
     * 自定义模型数据 (1.14+)
     */
    @JsonNames("custom-model-data", "cmd")
    var customModelData: Int? = null

    /**
     * 魔咒列表
     */
    @JsonNames("enchant", "enchantment", "enchantments")
    var enchants: MutableMap<@Contextual Enchantment, Int> = mutableMapOf()
        private set

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
    @JsonNames("hideflag", "hideflags", "hideFlag", "hideFlags", "flag", "flags", "itemFlag", "itemflag", "itemflags")
    var itemFlags: MutableSet<@Contextual ItemFlag> = mutableSetOf()

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
    @JsonNames("isUnbreakable", "isunbreakable")
    var unbreakable: Boolean = false

    /**
     * 物品属性修饰符
     */
    @JsonNames("attribute", "attributes", "modifier", "modifiers")
    var attributeModifiers: MutableMap<@Contextual Attribute,
            MutableList<@Contextual AttributeModifier>> = mutableMapOf()

    /**
     * 获取属性修饰符
     */
    fun getAttributeModifiers(attribute: Attribute): MutableList<AttributeModifier> =
        attributeModifiers.computeIfAbsent(attribute) { mutableListOf() }

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
        attributeModifiers[attribute] = mutableListOf()
    }

    fun removeAttributeModifiers(slot: EquipmentSlot) {
        attributeModifiers.forEach { (key, value) ->
            value.forEach {
                if (it.slot == slot) attributeModifiers[key]?.remove(it)
            }
        }
    }

    fun removeAttributeModifier(attribute: Attribute, modifier: AttributeModifier) {
        attributeModifiers[attribute]?.remove(modifier)
    }

    /**
     * 通过Bukkit.ItemMeta构造
     */
    constructor(meta: ItemMeta, replace: Boolean = true) {
        applyForm(meta, replace)
    }

    /**
     * 应用 Bukkit.ItemMeta
     * @param replace 如果元数据存在,是否替换 (默认true)
     */
    fun applyForm(
        meta: ItemMeta, replace: Boolean = true,
        /**
         * 值的设置
         * 使用变量的形式是为了自定义性
         */
        fDisplayName: Consumer<VItemMeta> = Consumer {
            if (it.displayName == null || replace)
                it.displayName = meta.getProperty<String?>("displayName")?.let { jsonToComponent(it) }
        },
        fLocalizedName: Consumer<VItemMeta> = Consumer {
            if (it.localizedName == null || replace)
                it.localizedName = meta.localizedName
        },
        fLore: Consumer<VItemMeta> = Consumer {
            if (it.lore.isEmpty() || replace)
                it.lore = meta.getProperty<List<String>?>("lore")
                    ?.map { jsonToComponent(it) }?.toMutableList() ?: mutableListOf()
        },
        fCustomModelData: Consumer<VItemMeta> = Consumer {
            if (it.customModelData == null || replace)
                it.customModelData = getProperty<Int>("customModelData")
        },
        fEnchants: Consumer<VItemMeta> = Consumer {
            if (it.enchants.isEmpty() || replace)
                it.enchants = meta.enchants
        },
        fItemFlags: Consumer<VItemMeta> = Consumer {
            if (it.itemFlags.isEmpty() || replace)
                it.itemFlags = meta.itemFlags
        },
        fUnbreakable: Consumer<VItemMeta> = Consumer {
            it.unbreakable = meta.isUnbreakable
        },
        fAttributeModifiers: Consumer<VItemMeta> = Consumer {
            if (it.attributeModifiers.isEmpty() || replace)
                it.attributeModifiers.apply {
                    meta.attributeModifiers?.forEach { key, value ->
                        addAttributeModifiers(key, value)
                    }
                }
        },
    ) {
        fDisplayName.accept(this)
        fLocalizedName.accept(this)
        fLore.accept(this)
        if (MinecraftVersion.isHigherOrEqual(MinecraftVersion.V1_14))
            fCustomModelData.accept(this)
        fEnchants.accept(this)
        fItemFlags.accept(this)
        fUnbreakable.accept(this)
        fAttributeModifiers.accept(this)
    }

//    /**
//     * 转换成ItemTag
//     * TODO 该如何设计这个函数
//     */
//    fun toItemTag(itemTag: ItemTag = ItemTag()) =
//        itemTag.apply {
//            val display = this.computeIfAbsent("display") { ItemTag() } as ItemTag
//            display["Name"] = ItemTagData(nmsComponent(displayName ?: Component.empty()))
//            display["Lore"] = lore.map {
//                ItemTagData(nmsComponent(displayName ?: Component.empty()))
//            }.toCollection(ItemTagList())
////            meta.setLocalizedName(localizedName)
////            if (MinecraftVersion.isHigherOrEqual(MinecraftVersion.V1_14)) {
////                setProperty("customModelData", customModelData?.let { Integer.valueOf(it) })
////            }
////            meta.enchants.clear()
////            this.enchants.forEach { meta.addEnchant(it.key, it.value, true) }
////            meta.itemFlags.clear()
////            meta.addItemFlags(*this.itemFlags.toSet().toTypedArray())
////            meta.isUnbreakable = this.unbreakable
////            meta.attributeModifiers = LinkedHashMultimap.create<Attribute, AttributeModifier>().apply {
////                this@VItemMeta.attributeModifiers.forEach { putAll(it.key, it.value) }
////            }
//        }

}