package cn.fd.ratziel.module.compat.impl

import cc.trixey.invero.common.Invero
import cc.trixey.invero.common.ItemSourceProvider
import cc.trixey.invero.core.Context
import cn.fd.ratziel.core.contextual.SimpleContext
import cn.fd.ratziel.module.compat.hook.HookInject
import cn.fd.ratziel.module.compat.hook.PluginHook
import cn.fd.ratziel.module.item.ItemManager
import cn.fd.ratziel.module.item.util.toItemStack
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * InveroHook
 *
 * @author TheFloodDragon
 * @since 2025/8/1 21:44
 */
object InveroHook : PluginHook {

    override val pluginName = "Invero"

    override val isHooked get() = plugin != null

    val plugin by lazy { Bukkit.getPluginManager().getPlugin(pluginName) }

    @HookInject
    private fun register() {
        Invero.API.getRegistry().registerItemSourceProvider("ratziel", ItemProviderForInvero)
    }

    object ItemProviderForInvero : ItemSourceProvider {

        override fun getItem(identifier: String, context: Any?): ItemStack? {
            val generator = ItemManager.registry[identifier] ?: return null
            val player = (context as? Context)?.viewer?.get<Player>()
            return if (player != null) {
                generator.build(SimpleContext(player)).get().toItemStack()
            } else {
                generator.build().get().toItemStack()
            }
        }

    }

}