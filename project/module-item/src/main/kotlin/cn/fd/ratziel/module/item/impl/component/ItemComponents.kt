@file:Suppress("NOTHING_TO_INLINE", "unused")

package cn.fd.ratziel.module.item.impl.component

import cn.altawk.nbt.tag.NbtCompound
import cn.fd.ratziel.module.item.api.component.ItemComponentType
import cn.fd.ratziel.module.item.impl.component.ComponentTypeBuilder.Companion.proxyClass
import cn.fd.ratziel.module.item.impl.component.transformers.*
import cn.fd.ratziel.module.item.impl.component.type.ItemEnchantmentMap
import cn.fd.ratziel.module.item.internal.serializers.MessageComponentSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import net.kyori.adventure.text.Component
import java.util.concurrent.CopyOnWriteArraySet

/**
 * ItemComponents
 *
 * 内置物品组件类型注册表。
 *
 * 这里集中定义项目内预置的 [ItemComponentType]，并在初始化时完成注册。
 * 每个组件类型会同时声明其 JSON、NBT 与 Minecraft 三套转换逻辑，
 * 供配置解析、NBT 读写以及原版组件访问等流程复用。
 *
 * @author TheFloodDragon
 * @since 2025/11/29 22:27
 */
object ItemComponents {

    /**
     * 物品组件注册表
     */
    val registry: MutableCollection<ItemComponentType<*>> = CopyOnWriteArraySet()

    // ** 添加后请同时在 ComponentHolderDSL 中添加委托属性 **

    /**
     * 物品自定义数据
     */
    @JvmField
    val CUSTOM_DATA: ItemComponentType<NbtCompound>

    /**
     * 物品自定义名称（覆盖默认名称，斜体显示）
     */
    @JvmField
    val DISPLAY_NAME: ItemComponentType<Component>

    /**
     * 物品基础名称（不覆盖，不显示为斜体）
     *
     * 1.20.5以下的版本叫做 本地化名称 (Localized Name)
     */
    @JvmField
    val ITEM_NAME: ItemComponentType<Component>

    /**
     * 物品描述 Lore
     */
    @JvmField
    val LORE: ItemComponentType<List<Component>>

    /**
     * 物品最大耐久值 (仅 1.20.5+)
     */
    @JvmField
    val MAX_DAMAGE: ItemComponentType<Int>

    /**
     * 物品修复消耗
     */
    @JvmField
    val REPAIR_COST: ItemComponentType<Int>

    /**
     * 物品附魔
     */
    @JvmField
    val ENCHANTMENTS: ItemComponentType<ItemEnchantmentMap>

    /**
     * 是否覆盖附魔光效显示 (仅 1.20.5+)
     */
    @JvmField
    val GLINT_OVERRIDE: ItemComponentType<Boolean>

    /**
     * 物品是否不可破坏
     */
    @JvmField
    val UNBREAKABLE: ItemComponentType<Boolean>

    // TODO .... more ... and .. more

    /**
     * 注册内置组件类型。
     */
    init {
        CUSTOM_DATA = r("custom-data", NbtCompound.serializer()) {
            serialJsonEntry()
            serialNbtEntry("minecraft:custom_data")
            minecraftKeyed("custom_data") { proxyClass("CustomDataE2MTransformer") }
        }
        DISPLAY_NAME = r("display-name", MessageComponentSerializer) {
            serialJsonEntry("displayName", "name", "custom_name", "customName")
            serialNbtEntry(if (v >= 12005) "minecraft:custom_name" else "display.Name")
            minecraftKeyed("custom_name") { MessageE2MTransformer }
        }
        ITEM_NAME = r("item-name", MessageComponentSerializer) {
            serialJsonEntry("itemName", "localized-name", "localizedName")
            serialNbtEntry(if (v >= 12005) "minecraft:item_name" else "display.LocName")
            minecraftKeyed("item_name") { MessageE2MTransformer }
        }
        LORE = r("lore", ListSerializer(MessageComponentSerializer)) {
            serialJsonEntry("lores")
            serialNbtEntry(if (v >= 12005) "minecraft:lore" else "Lore")
            minecraftKeyed("lore") { proxyClass("ItemLoreE2MTransformer") }
        }
        MAX_DAMAGE = r("max-damage", Int.serializer()) {
            isSupported = v >= 12005
            serialJsonEntry("maxDamage", "maxDurability", "max-durability", "durability")
            serialNbtEntry("minecraft:max_damage")
            minecraftKeyed("max_damage") { NoneE2MTransformer() }
        }
        REPAIR_COST = r("repair-cost", Int.serializer()) {
            serialJsonEntry("repairCost")
            serialNbtEntry(if (v >= 12005) "minecraft:repair_cost" else "RepairCost")
            minecraftKeyed("repair_cost") { NoneE2MTransformer() }
        }
        ENCHANTMENTS = r("enchantments", ItemEnchantmentMap.serializer()) {
            serialJsonEntry("enchant", "enchants", "enchantment")
            nbt(EnchantmentsNbtTransformer)
            minecraft(EnchantmentsMinecraftTransformer)
        }
        GLINT_OVERRIDE = r("glint-override", Boolean.serializer()) {
            isSupported = v >= 12005
            serialJsonEntry("glintOverride", "glint", "enchantmentGlintOverride", "enchantment-glint-override")
            serialNbtEntry("minecraft:enchantment_glint_override")
            minecraftKeyed("enchantment_glint_override") { NoneE2MTransformer() }
        }
        UNBREAKABLE = r("unbreakable", Boolean.serializer()) {
            serialJsonEntry("isUnbreakable")
            nbt(UnbreakableNbtTransformer)
            if (v >= 12005) minecraft(proxyClass("UnbreakableMinecraftTransformer"))
        }
    }

    /**
     * 以 reified 泛型形式注册组件类型。
     */
    private inline fun <reified T : Any> r(id: String, serializer: KSerializer<T>, noinline block: ComponentTypeBuilder<T>.() -> Unit = {}) =
        this.r(id, T::class.java, serializer, block)

    /**
     * 构建并注册一个组件类型。
     */
    private fun <T : Any> r(id: String, type: Class<T>, serializer: KSerializer<T>, block: ComponentTypeBuilder<T>.() -> Unit): ItemComponentType<T> {
        val builder = ComponentTypeBuilder(id, type, serializer).apply(block)
        val type = builder.build()
        registry.add(type)
        return type
    }

}