package cn.fd.ratziel.module.compat.impl

import cn.fd.ratziel.module.compat.hook.HookInject
import cn.fd.ratziel.module.compat.hook.PluginHook
import cn.fd.ratziel.module.item.ItemRegistry
import org.bukkit.Bukkit

/**
 * NeigeItemsHook
 *
 * @author TheFloodDragon
 * @since 2025/5/25 11:22
 */
object NeigeItemsHook : PluginHook {

    override val pluginName = "NeigeItems"

    override val isHooked get() = plugin != null

    val plugin by lazy { Bukkit.getPluginManager().getPlugin(pluginName) }

    @HookInject
    private fun register() {
        ItemRegistry.registerSource(NeigeItemsSource)
    }

}