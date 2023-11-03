package cn.fd.ratziel.library.folia

import com.tcoded.folialib.FoliaLib
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.event.player.PlayerTeleportEvent
import taboolib.platform.BukkitPlugin

val foliaLib by lazy { FoliaLib(BukkitPlugin.getInstance()) }

val scheduler by lazy { foliaLib.impl }

/**
 * 各平台确认
 */
fun isFolia() = foliaLib.isFolia
fun isPaper() = foliaLib.isPaper
fun isSpigot() = foliaLib.isSpigot
fun isBukkit() = foliaLib.isUnsupported

/**
 * 扩展函数
 */
fun Entity.teleportQuick(
    location: Location,
    cause: PlayerTeleportEvent.TeleportCause = PlayerTeleportEvent.TeleportCause.PLUGIN,
) = Folia.teleport(this, location, cause)