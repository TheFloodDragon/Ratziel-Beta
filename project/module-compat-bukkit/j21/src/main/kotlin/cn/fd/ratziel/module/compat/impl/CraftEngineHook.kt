package cn.fd.ratziel.module.compat.impl

import cn.fd.ratziel.module.compat.hook.HookInject
import cn.fd.ratziel.module.compat.hook.PluginHook
import cn.fd.ratziel.module.item.ItemRegistry
import org.bukkit.Bukkit

/**
 * CraftEngineHook
 *
 * @author TheFloodDragon
 * @since 2025/8/2 20:16
 */
object CraftEngineHook : PluginHook {

    override val pluginName = "CraftEngine"

    override val isHooked by lazy {
        Bukkit.getPluginManager().getPlugin(pluginName) != null
    }

    @HookInject
    private fun register() {
        ItemRegistry.registerSource(CraftEngineSource)
    }

}