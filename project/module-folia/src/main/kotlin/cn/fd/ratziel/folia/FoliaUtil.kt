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


fun submitFolia(delay: Long = 0, period: Long = 0, action: FoliaTaskCallBack.() -> Unit): FoliaTaskCallBack {
    val scheduler = FoliaAPI.getScheduler()
    val back = FoliaTaskCallBack()
    back.scheduler = scheduler
    if (period > 0) {
        scheduler.runTaskTimer({
            action.invoke(back)
        }, delay, period).apply {
            back.task = this
        }
    }
    if (delay <= 0) {
        scheduler.runTask {
            action.invoke(back)
        }.apply {
            back.task = this
        }
    } else {
        scheduler.runTaskLater({
            action.invoke(back)
        }, delay).apply {
            back.task = this
        }
    }
    return back
}
