package cn.fd.ratziel.compat.hook.impl.trchat

import cn.fd.ratziel.compat.ClassLoaderProvider
import cn.fd.ratziel.compat.hook.HookInject
import cn.fd.ratziel.compat.hook.HookManager
import cn.fd.ratziel.compat.hook.ManagedPluginHook
import org.bukkit.Bukkit

/**
 * TrChatHook
 *
 * @author TheFloodDragon
 * @since 2024/2/17 11:28
 */
@HookInject
object TrChatHook : ManagedPluginHook {

    override val pluginName = "TrChat"

    override fun isHookable() = plugin != null

    val plugin get() = Bukkit.getPluginManager().getPlugin(pluginName)

    @Suppress("SpellCheckingInspection")
    override val bindProvider = ClassLoaderProvider { name ->
        if (name.startsWith("me.arasple.mc.trchat"))
            me.arasple.mc.trchat.taboolib.common.classloader.IsolatedClassLoader.INSTANCE
        else null
    }

    override val managedClasses = HookManager.buildHookClasses(this::class.java)

}