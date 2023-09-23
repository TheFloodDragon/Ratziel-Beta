package cn.fd.ratziel.folia

import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.event.player.PlayerTeleportEvent

/**
 * 传送
 */
fun Entity.teleportF(
    target: Location,
    cause: PlayerTeleportEvent.TeleportCause = PlayerTeleportEvent.TeleportCause.PLUGIN,
) =
    if (FoliaAPI.isFolia()) {
        this.teleportAsync(target, cause).isDone
    } else this.teleport(target, cause)