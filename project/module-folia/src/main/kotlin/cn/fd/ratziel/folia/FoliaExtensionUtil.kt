package cn.fd.ratziel.folia

import cn.fd.ratziel.folia.lib.ProxyScheduler
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.event.player.PlayerTeleportEvent

/**
 * 传送
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
fun Entity.teleport(
    target: Location,
    cause: PlayerTeleportEvent.TeleportCause = PlayerTeleportEvent.TeleportCause.PLUGIN,
) =
    if (FoliaAPI.isFolia()) {
        this.teleportAsync(target, cause).isDone
    } else this.teleport(target, cause)


/**
 * 调度器
 */
fun Bukkit.getScheduler(): ProxyScheduler = FoliaAPI.getScheduler()
fun Bukkit.getScheduler(entity: Entity, retired: Runnable): ProxyScheduler = FoliaAPI.getScheduler(entity,retired)
fun Bukkit.getScheduler(location: Location): ProxyScheduler = FoliaAPI.getScheduler(location)
fun Bukkit.getScheduler(isGlobal: Boolean): ProxyScheduler = FoliaAPI.getScheduler(isGlobal)