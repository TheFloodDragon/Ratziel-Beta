package cn.fd.ratziel.folia.lib

/**
 * ProxyScheduler
 *
 * @author TheFloodDragon
 * @since 2023/9/23 12:11
 */
interface ProxyScheduler {
    fun runTask(runnable: Runnable): ProxyTask
    fun runTaskLater(runnable: Runnable, delay: Long): ProxyTask
    fun runTaskTimer(runnable: Runnable, delay: Long, period: Long): ProxyTask
    fun runTaskAsynchronously(runnable: Runnable): ProxyTask
    fun runTaskLaterAsynchronously(runnable: Runnable, delay: Long): ProxyTask
    fun runTaskTimerAsynchronously(runnable: Runnable, delay: Long, period: Long): ProxyTask
}