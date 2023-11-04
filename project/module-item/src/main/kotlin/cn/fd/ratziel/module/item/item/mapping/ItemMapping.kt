package cn.fd.ratziel.module.item.item.mapping

import java.util.function.Supplier

/**
 * ItemMapping - 物品NBT标签映射表
 *
 * @author TheFloodDragon
 * @since 2023/11/4 11:41
 */
enum class ItemMapping(private val value: String) : Supplier<String> {

    DISPLAY("display"),
    DISPLAY_NAME("Name"),
    DISPLAY_LORE("Lore");

    override fun get() = value

}