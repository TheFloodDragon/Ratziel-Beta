package cn.fd.ratziel.library.folia

import com.tcoded.folialib.impl.ServerImplementation
import com.tcoded.folialib.wrapper.task.WrappedTask
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.event.player.PlayerTeleportEvent
import java.util.function.Consumer

object Folia : ServerImplementation by scheduler {

    /**
     * 传送实体 TODO Just Waiting
     *
     * @param entity 需要传送的实体
     * @param location 传送目的地
     * @param cause 传送原因
     * @return 传送是否成功
     */
    @JvmStatic
    @Deprecated(
        "Unsafe, waiting the PR: https://github.com/TechnicallyCoded/FoliaLib/pull/10", ReplaceWith(
            "teleportAsync(entity as org.bukkit.entity.Player, location)",
            "cn.fd.ratziel.library.folia.Folia.teleportAsync",
        )
    )
    fun teleport(
        entity: Entity,
        location: Location,
        cause: PlayerTeleportEvent.TeleportCause = PlayerTeleportEvent.TeleportCause.PLUGIN,
    ) = teleportAsync(entity as org.bukkit.entity.Player, location)
//        if (isFolia()) {
//            entity.teleportAsync(location, cause).isDone
//        } else entity.teleport(location, cause)

    @JvmStatic
    fun runTask(consumer: Consumer<WrappedTask>) = runNextTick(consumer).isDone

    @JvmStatic
    fun runTask(runnable: Runnable) = runNextTick { runnable.run() }.isDone

}
