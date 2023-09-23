package cn.fd.ratziel.folia.lib.wrapper

import cn.fd.ratziel.folia.lib.ProxyTask
import io.papermc.paper.threadedregions.scheduler.ScheduledTask

/**
 * ProxyFoliaTask
 *
 * @author TheFloodDragon
 * @since 2023/9/23 12:16
 */
class ProxyFoliaTask(private val task: ScheduledTask) : ProxyTask {

    override fun getOwningPlugin() = task.owningPlugin

    override fun cancel() {
        task.cancel()
    }

    override fun isCancelled() = task.isCancelled()

    override fun isTimerTask() = task.isRepeatingTask

    override fun isAsyncTask() = true
}