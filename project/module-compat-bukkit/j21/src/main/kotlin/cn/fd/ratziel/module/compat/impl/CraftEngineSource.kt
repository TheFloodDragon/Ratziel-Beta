package cn.fd.ratziel.module.compat.impl

import cn.fd.ratziel.core.contextual.ArgumentContext
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.module.item.api.NeoItem
import cn.fd.ratziel.platform.bukkit.util.player
import net.momirealms.craftengine.bukkit.api.CraftEngineItems
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

    override fun generateItem(element: Element, context: ArgumentContext): NeoItem? =
        readName(element.property)
            ?.let { CraftEngineItems.byId(it).warnOnNull(it) }
            ?.let { item ->
                ((context.player() as? Player)
                    ?.let { item.buildBukkitItem(it) }
                    ?: item.buildBukkitItem())
                    .asCompatible()
            }

    override fun isMine(item: ItemStack) = CraftEngineItems.isCustomItem(item)

}