package cn.fd.ratziel.module.itemengine.mapping

import java.util.function.Supplier

/**
 * ItemMapping - 物品NBT标签映射表
 *
 * @author TheFloodDragon
 * @since 2023/11/4 11:41
 */
@Suppress("SpellCheckingInspection")
enum class ItemMapping(
    val fieldName: String,
    val default: String,
    internal val key: RefItemMeta.RefItemMetaKey = RefItemMeta.RefItemMetaKey(fieldName),
    internal val value: String = key.NMS_NAME ?: default,
) : Supplier<String> {

    DISPLAY("DISPLAY", "display"),
    DISPLAY_NAME("NAME", "Name"),
    DISPLAY_LORE("LORE", "Lore"),
    DISPLAY_LOCAL_NAME("LOCNAME", "LocName"),
    ENCHANTMENTS("ENCHANTMENTS", "Enchantments"),
    ATTRIBUTE_MODIFIERS("ATTRIBUTES", "AttributeModifiers"),
    CUSTOM_MODEL_DATA("CUSTOM_MODEL_DATA", "CustomModelData"),
    HIDE_FLAG("HIDEFLAGS", "HideFlags"),
    REPAIR_COST("REPAIR", "RepairCost"),
    DAMAGE("DAMAGE", "Damage"),
    UNBREAKABLE("UNBREAKABLE", "Unbreakable");

    override fun get() = value

}