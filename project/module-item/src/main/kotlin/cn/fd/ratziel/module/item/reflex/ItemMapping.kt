package cn.fd.ratziel.module.item.reflex

/**
 * ItemMapping - 物品NBT标签映射表
 * 方法一: 反射获取 CraftMetaItem 内ItemMetaKey类型的静态字段
 *
 * @author TheFloodDragon
 * @since 2023/11/4 11:41
 */
@Suppress("SpellCheckingInspection")
enum class ItemMapping(
    private val method0: String,
    private val fallback0: String?,
    private val fallback1: String?,
) {

    /**
     * 1.20.4-: static final ItemMetaKey
     * 1.20.5+: static final ItemMetaKeyType<?>
     *     static final class ItemMetaKeyType<T> extends ItemMetaKey
     */

    DISPLAY("DISPLAY", "display", null),
    DISPLAY_NAME("NAME", "Name", "display-name"),
    DISPLAY_LORE("LORE", "Lore", "lore"),
    DISPLAY_LOCAL_NAME("LOCNAME", "LocName", null),
    ENCHANTMENTS("ENCHANTMENTS", "Enchantments", "enchants"),
    ATTRIBUTE_MODIFIERS("ATTRIBUTES", "AttributeName", "attribute-modifiers"),
    CUSTOM_MODEL_DATA("CUSTOM_MODEL_DATA", "CustomModelData", "custom-model-data"),
    HIDE_FLAG("HIDEFLAGS", "HideFlags", "ItemFlags"),
    REPAIR_COST("REPAIR", "RepairCost", "repair-cost"),
    DAMAGE("DAMAGE", "Damage", "Damage"),
    UNBREAKABLE("UNBREAKABLE", "Unbreakable", "Unbreakable"),
    ITEM_NAME("ITEM_NAME", null, "item-name"),
    MAX_DAMAGE("MAX_DAMAGE", null, "max-damage"),
    FOOD("FOOD", null, "food"),
    RARITY("RARITY", null, "rarity"),
    MAX_STACK_SIZE("MAX_STACK_SIZE", null, "max-stack-size"),
    FIRE_RESISTANT("FIRE_RESISTANT", null, "fire-resistant"),
    ENCHANTMENT_GLINT_OVERRIDE("ENCHANTMENT_GLINT_OVERRIDE", null, "enchantment-glint-override"),
    HIDE_TOOLTIP("HIDE_TOOLTIP", null, "hide-tool-tip");

    internal val obcKey by lazy {
        RefItemMeta.RefItemMetaKey(method0)
    }

    /**
     * 映射后的名字
     */
    val mapping by lazy {
        //TODO
        obcKey.NMS_NAME ?: error("Unknown fieldName \"${obcKey.fieldName}\" in CraftMetaItem")
    }

}