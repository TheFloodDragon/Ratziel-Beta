package cn.fd.ratziel.module.itemengine.mapping

import java.util.function.Supplier

/**
 * ItemMapping - 物品NBT标签映射表
 * 方法一: 反射获取 CraftMetaItem 内ItemMetaKey类型的静态字段
 *
 * @author TheFloodDragon
 * @since 2023/11/4 11:41
 */
@Suppress("SpellCheckingInspection")
enum class ItemMapping(
    private val method0: String
) : Supplier<String> {

    DISPLAY("DISPLAY"),
    DISPLAY_NAME("NAME"),
    DISPLAY_LORE("LORE"),
    DISPLAY_LOCAL_NAME("LOCNAME"),
    ENCHANTMENTS("ENCHANTMENTS"),
    ATTRIBUTE_MODIFIERS("ATTRIBUTES"),
    CUSTOM_MODEL_DATA("CUSTOM_MODEL_DATA"),
    HIDE_FLAG("HIDEFLAGS"),
    REPAIR_COST("REPAIR"),
    DAMAGE("DAMAGE"),
    UNBREAKABLE("UNBREAKABLE");

    internal val obcKey by lazy {
        RefItemMeta.RefItemMetaKey(method0)
    }

    val value by lazy {
        obcKey.NMS_NAME ?: error("Unknown fieldName \"${obcKey.fieldName}\" in CraftMetaItem")
    }

    override fun get() = value

}