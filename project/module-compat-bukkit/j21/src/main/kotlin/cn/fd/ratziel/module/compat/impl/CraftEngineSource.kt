package cn.fd.ratziel.module.compat.impl

import cn.fd.ratziel.core.contextual.ArgumentContext
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.module.item.api.NeoItem
import cn.fd.ratziel.platform.bukkit.util.player
import net.momirealms.craftengine.bukkit.api.CraftEngineItems
import net.momirealms.craftengine.bukkit.plugin.BukkitCraftEngine
import net.momirealms.craftengine.core.util.Key
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * CraftEngineSource
 *
 * @author TheFloodDragon
 * @since 2025/8/2 20:18
 */
object CraftEngineSource : CompatibleItemSource(
    CraftEngineHook.pluginName,
    "ce"
) {

    override fun generateItem(element: Element, context: ArgumentContext): NeoItem? {
        // 获取名称
        val name = readName(element.property) ?: return null
        // 生成物品
        val player = (context.player() as? Player)?.let { BukkitCraftEngine.instance().adapt(it) }
        val customItem = CraftEngineItems.byId(Key.of(name))
            .warnOnNull(name) ?: return null
        val itemStack = customItem.buildItemStack(player)
        return itemStack.asCompatible()
    }

    override fun isMine(item: ItemStack) = CraftEngineItems.isCustomItem(item)

}