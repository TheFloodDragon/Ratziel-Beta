package cn.fd.ratziel.module.itemengine.item.mapping

import java.util.function.Supplier

/**
 * ItemMapping - 物品NBT标签映射表
 *
 * @author TheFloodDragon
 * @since 2023/11/4 11:41
 */
@Deprecated("以后在别的地方")
enum class ItemMapping(private val value: String) : Supplier<String> {

    DISPLAY("display"),
    DISPLAY_NAME("Name"),
    DISPLAY_LORE("Lore"),
    DISPLAY_LOCAL_NAME("LocName");

    override fun get() = value

}