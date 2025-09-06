package cn.fd.ratziel.module.compat.impl

import cn.fd.ratziel.core.contextual.ArgumentContext
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.module.item.api.NeoItem
import cn.fd.ratziel.platform.bukkit.util.player
import org.bukkit.inventory.ItemStack

/**
 * NeigeItemsSource
 *
 * @author TheFloodDragon
 * @since 2025/5/25 11:23
 */
object NeigeItemsSource : CompatibleItemSource(
    NeigeItemsHook.pluginName,
    "ni"
) {

    typealias NeigeItemManager = pers.neige.neigeitems.manager.ItemManager

    override fun generateItem(element: Element, context: ArgumentContext): NeoItem? {
        // 获取名称
        val name = readName(element.property) ?: return null
        // 生成物品
        val generator = NeigeItemManager.getItem(name)
            ?.warnOnNull(name) ?: return null
        val itemStack = generator.getItemStack(context.player(), mutableMapOf()) ?: return null
        return itemStack.asCompatible()
    }

    override fun isMine(item: ItemStack) = NeigeItemManager.isNiItem(item) != null

}