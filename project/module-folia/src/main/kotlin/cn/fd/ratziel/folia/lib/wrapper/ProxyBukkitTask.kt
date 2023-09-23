package cn.fd.ratziel.folia.lib.wrapper

import cn.fd.ratziel.folia.lib.ProxyTask
import org.bukkit.scheduler.BukkitTask

/**
 * ProxyBukkitTask
 *
 * @author TheFloodDragon
 * @since 2023/9/23 12:13
 */
class ProxyBukkitTask(private val task: BukkitTask, val isTimer: Boolean) : ProxyTask {

    override fun getOwningPlugin() = task.owner

    override fun cancel() {
        task.cancel()
    }

    override fun isCancelled() = task.isCancelled

    override fun isTimerTask() = isTimer

    override fun isAsyncTask() = task.isSync
}