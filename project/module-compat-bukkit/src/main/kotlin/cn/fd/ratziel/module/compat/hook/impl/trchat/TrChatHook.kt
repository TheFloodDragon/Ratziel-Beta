package cn.fd.ratziel.module.compat.hook.impl.trchat

import cn.fd.ratziel.module.compat.hook.HookManager
import cn.fd.ratziel.module.compat.hook.ManagedPluginHook
import cn.fd.ratziel.module.compat.util.ClassProvider
import me.arasple.mc.trchat.taboolib.common.classloader.IsolatedClassLoader
import org.bukkit.Bukkit

/**
 * TrChatHook
 *
 * @author TheFloodDragon
 * @since 2024/2/17 11:28
 */
object TrChatHook : ManagedPluginHook {

    override val pluginName = "TrChat"

    override val isHooked = plugin != null

    val plugin get() = Bukkit.getPluginManager().getPlugin(pluginName)

    @Suppress("SpellCheckingInspection")
    override val bindProvider = ClassProvider { name ->
        if (name.startsWith("me.arasple.mc.trchat"))
            IsolatedClassLoader.INSTANCE.loadClass(name, false, false)
        else null
    }

    override val managedClasses = HookManager.buildHookClasses(this::class.java)

}