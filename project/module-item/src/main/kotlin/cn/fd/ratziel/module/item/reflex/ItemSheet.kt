package cn.fd.ratziel.module.item.reflex

/**
 * ItemSheet - 物品NBT标签映射表
 *
 * @author TheFloodDragon
 * @since 2023/11/4 11:41
 */
object ItemSheet {

    val DISPLAY by mapping("DISPLAY")
    val DISPLAY_NAME by mapping("DISPLAY_NAME")
    val DISPLAY_LORE by mapping("DISPLAY_LORE")
    val DISPLAY_LOCAL_NAME by mapping("DISPLAY_LOCAL_NAME")
    val ENCHANTMENTS by mapping("ENCHANTMENTS")
    val ATTRIBUTE_MODIFIERS by mapping("ATTRIBUTE_MODIFIERS")
    val CUSTOM_MODEL_DATA by mapping("CUSTOM_MODEL_DATA")
    val HIDE_FLAG by mapping("HIDE_FLAG")
    val REPAIR_COST by mapping("REPAIR_COST")
    val DAMAGE by mapping("DAMAGE")
    val UNBREAKABLE by mapping("UNBREAKABLE")
    val ITEM_NAME by mapping("ITEM_NAME")
    val MAX_DAMAGE by mapping("MAX_DAMAGE")
    val FOOD by mapping("FOOD")
    val RARITY by mapping("RARITY")
    val MAX_STACK_SIZE by mapping("MAX_STACK_SIZE")
    val FIRE_RESISTANT by mapping("FIRE_RESISTANT")
    val ENCHANTMENT_GLINT_OVERRIDE by mapping("ENCHANTMENT_GLINT_OVERRIDE")
    val HIDE_TOOLTIP by mapping("HIDE_TOOLTIP")

    fun mapping(name: String): Lazy<String> = lazy { ItemMapper.map(name) }

}