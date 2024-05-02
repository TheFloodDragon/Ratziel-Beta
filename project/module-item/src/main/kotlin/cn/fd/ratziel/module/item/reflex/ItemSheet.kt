package cn.fd.ratziel.module.item.reflex

/**
 * ItemSheet - 物品NBT标签映射表
 *
 * @author TheFloodDragon
 * @since 2023/11/4 11:41
 */
object ItemSheet {

    // Universal
    val CUSTOM_DATA by mapping("CUSTOM_DATA")
    val DISPLAY_NAME by mapping("DISPLAY_NAME")
    val DISPLAY_LORE by mapping("DISPLAY_LORE")
    val ENCHANTMENTS by mapping("ENCHANTMENTS")
    val ATTRIBUTE_MODIFIERS by mapping("ATTRIBUTE_MODIFIERS")
    val CUSTOM_MODEL_DATA by mapping("CUSTOM_MODEL_DATA")
    val REPAIR_COST by mapping("REPAIR_COST")
    val DAMAGE by mapping("DAMAGE")
    val UNBREAKABLE by mapping("UNBREAKABLE")

    // Custom Features And 1.20.5+
    val MAX_DAMAGE by mapping("MAX_DAMAGE")
    val MAX_STACK_SIZE by mapping("MAX_STACK_SIZE")

    // Only 1.20.5+
    val FOOD by mapping("FOOD")
    val ITEM_NAME by mapping("ITEM_NAME")
    val RARITY by mapping("RARITY")
    val FIRE_RESISTANT by mapping("FIRE_RESISTANT")
    val ENCHANTMENT_GLINT_OVERRIDE by mapping("ENCHANTMENT_GLINT_OVERRIDE")
    val HIDE_TOOLTIP by mapping("HIDE_TOOLTIP")

    // Only 1.20.5-
    val DISPLAY by mapping("DISPLAY")
    val DISPLAY_LOCAL_NAME by mapping("DISPLAY_LOCAL_NAME")

    // 1.20.5- But be retained by CraftBukkit
    val HIDE_FLAG by mapping("HIDE_FLAG")

    fun mapping(name: String): Lazy<String> = lazy { ItemMapper.map(name) }

}