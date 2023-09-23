package cn.fd.ratziel.folia

import cn.fd.ratziel.folia.lib.ProxyScheduler
import cn.fd.ratziel.folia.lib.wrapper.ProxyBukkitScheduler
import cn.fd.ratziel.folia.lib.wrapper.ProxyFoliaScheduler
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.plugin.Plugin
import taboolib.platform.BukkitPlugin

object FoliaAPI {

    val plugin by lazy {
        BukkitPlugin.getInstance()
    }

    val platform by lazy { ServerPlatform.infer() }

    /**
     * 各平台确认
     */
    fun isFolia() = platform == ServerPlatform.FOLIA
    fun isPaper() = platform == ServerPlatform.PAPER
    fun isSpigot() = platform == ServerPlatform.SPIGOT
    fun isBukkit() = platform == ServerPlatform.BUKKIT

    /**
     * 传送实体
     *
     * @param entity 需要传送的实体
     * @param target 传送目的地
     * @param cause 传送原因
     * @return 传送是否成功
     */
    fun teleport(
        entity: Entity,
        target: Location,
        cause: PlayerTeleportEvent.TeleportCause = PlayerTeleportEvent.TeleportCause.PLUGIN,
    ) =
        if (isFolia()) {
            entity.teleportAsync(target, cause).isDone
        } else entity.teleport(target, cause)


    /**
     * 获得调度程序管理器, 参数仅影在Folia中的实现, 无参代表getAsyncScheduler()。
     *
     * @return 调度管理器
     */
    fun getScheduler(): ProxyScheduler =
        if (isFolia()) {
            ProxyFoliaScheduler(plugin, false)
        } else ProxyBukkitScheduler(plugin)

    /**
     * 获得调度程序管理器, 参数仅影在Folia中的实现, 此处获得实体调度器.
     *
     * @param entity  操作的实体
     * @param retired 回调函数. 当执行时实体变为null时执行的方法。
     * @return 调度管理器
     */
    fun getScheduler(entity: Entity, retired: Runnable): ProxyScheduler {
        return if (isFolia()) {
            ProxyFoliaScheduler(plugin, entity, retired)
        } else ProxyBukkitScheduler(plugin)
    }

    /**
     * 获得调度程序管理器, 参数仅影在Folia中的实现, 此处获得区块调度器.
     *
     * @param location 区域的位置
     * @return 调度管理器
     */
    fun getScheduler(location: Location): ProxyScheduler {
        return if (isFolia()) {
            ProxyFoliaScheduler(plugin, location)
        } else ProxyBukkitScheduler(plugin)
    }

    /**
     * 获得调度程序管理器, 参数仅影在Folia中的实现, 此处获得全局调度器.
     *
     * @param isGlobal 是否为全局， 如为false则等同无参。
     * @return 调度管理器
     */
    fun getScheduler(isGlobal: Boolean): ProxyScheduler {
        return if (isFolia()) {
            ProxyFoliaScheduler(plugin, isGlobal)
        } else ProxyBukkitScheduler(plugin)
    }

    /**
     * 取消所有调度任务
     *
     * @param plugin 插件实例
     */
    fun cancelTask(plugin: Plugin) {
        if (isFolia()) {
            Bukkit.getGlobalRegionScheduler().cancelTasks(plugin)
            Bukkit.getAsyncScheduler().cancelTasks(plugin)
        } else {
            Bukkit.getScheduler().cancelTasks(plugin)
        }
    }

}
