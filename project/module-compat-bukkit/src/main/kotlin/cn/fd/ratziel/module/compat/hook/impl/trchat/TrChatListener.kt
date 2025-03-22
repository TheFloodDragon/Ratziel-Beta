package cn.fd.ratziel.module.compat.hook.impl.trchat

import cn.fd.ratziel.module.compat.hook.HookInject
import me.arasple.mc.trchat.api.event.TrChatEvent
import me.arasple.mc.trchat.taboolib.common.PrimitiveLoader
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import taboolib.common.platform.Ghost

/**
 * TrChatListener (不准将当前插件中任何被隔离加载的传入别的插件)
 *
 * @author TheFloodDragon
 * @since 2024/2/17 11:51
 */
@Ghost
object TrChatListener {

    private val impl by lazy {
        object : Listener {
            @EventHandler
            fun onChat(event: TrChatEvent) {
//                println(event.message)
                println(event)
            }
        }
    }

    /**
     * 注册监听事件
     */
    @HookInject
    fun register() {
        println("[R] TrChat | Test Hook")
        println(this::class.java.classLoader)
        println(PrimitiveLoader.TABOOLIB_GROUP)
        Bukkit.getPluginManager().registerEvents(impl, TrChatHook.plugin!!)
    }


}