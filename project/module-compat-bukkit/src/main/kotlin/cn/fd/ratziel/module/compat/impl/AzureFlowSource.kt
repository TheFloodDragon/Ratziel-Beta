package cn.fd.ratziel.module.compat.impl

import cn.fd.ratziel.core.contextual.ArgumentContext
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.module.item.api.NeoItem
import cn.fd.ratziel.platform.bukkit.util.player
import io.rokuko.azureflow.api.AzureFlowAPI
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * AzureFlowSource
 *
 * @author TheFloodDragon
 * @since 2025/4/4 15:26
 */
object AzureFlowSource : CompatibleItemSource(
    AzureFlowHook.pluginName,
    "af"
) {

    override fun generateItem(element: Element, context: ArgumentContext): NeoItem? {
        // 获取名称
        val name = readName(element.property) ?: return null
        // 生成物品
        val factory = AzureFlowAPI.getFactory(name).warnOnNull(name) ?: return null
        val itemStack = factory.build().itemStack(context.player() as? Player)
        return itemStack.asCompatible()
    }

    override fun isMine(item: ItemStack) = AzureFlowAPI.toItem(item) != null

}