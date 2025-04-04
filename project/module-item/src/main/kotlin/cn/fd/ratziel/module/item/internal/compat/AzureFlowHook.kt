package cn.fd.ratziel.module.item.internal.compat

import cn.fd.ratziel.module.compat.hook.HookInject
import cn.fd.ratziel.module.compat.hook.PluginHook
import cn.fd.ratziel.module.item.ItemRegistry
import org.bukkit.Bukkit

/**
 * AzureFlowHook
 *
 * @author TheFloodDragon
 * @since 2025/4/4 17:24
 */
object AzureFlowHook : PluginHook {

    override val pluginName = "AzureFlow"

    override val isHooked get() = plugin != null

    val plugin by lazy { Bukkit.getPluginManager().getPlugin(pluginName) }

    @HookInject
    private fun registerSource() {
        ItemRegistry.sources.add(AzureFlowSource)
    }

}