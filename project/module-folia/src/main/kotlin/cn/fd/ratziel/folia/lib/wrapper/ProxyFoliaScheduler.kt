package cn.fd.ratziel.folia.lib.wrapper

import cn.fd.ratziel.folia.lib.ProxyScheduler
import cn.fd.ratziel.folia.lib.ProxyTask
import cn.fd.ratziel.folia.lib.SchedulerType
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.plugin.Plugin
import java.util.concurrent.TimeUnit

/**
 * ProxyFoliaScheduler
 *
 * @author TheFloodDragon
 * @since 2023/9/23 12:20
 */
class ProxyFoliaScheduler : ProxyScheduler {

    private var plugin: Plugin

    private lateinit var schedulerType: SchedulerType
    private lateinit var location: Location
    private lateinit var entity: Entity
    private lateinit var retired: Runnable

    private constructor(plugin: Plugin) {
        this.plugin = plugin
    }

    constructor(plugin: Plugin, isGlobal: Boolean) : this(plugin) {
        schedulerType = if (isGlobal) SchedulerType.GLOBAL else SchedulerType.ASYNC
    }

    constructor(plugin: Plugin, location: Location) : this(plugin) {
        this.location = location
        schedulerType = SchedulerType.REGION
    }

    constructor(plugin: Plugin, entity: Entity, retired: Runnable) : this(plugin) {
        this.entity = entity
        this.retired = retired
        schedulerType = SchedulerType.ENTITY
    }

    override fun runTask(runnable: Runnable): ProxyTask = ProxyFoliaTask(when (schedulerType) {
        SchedulerType.ENTITY -> entity.scheduler.run(plugin, { runnable.run() }, retired)
        SchedulerType.GLOBAL -> Bukkit.getGlobalRegionScheduler().run(plugin) { runnable.run() }
        SchedulerType.REGION -> Bukkit.getRegionScheduler().run(plugin, location) { runnable.run() }
        SchedulerType.ASYNC -> Bukkit.getAsyncScheduler().runNow(plugin) { runnable.run() }
    }!!)

    override fun runTaskLater(runnable: Runnable, delay: Long): ProxyTask = ProxyFoliaTask(
        when (schedulerType) {
            SchedulerType.ENTITY -> entity.scheduler.runDelayed(plugin, { runnable.run() }, retired, delay)
            SchedulerType.GLOBAL -> Bukkit.getGlobalRegionScheduler().runDelayed(plugin, { runnable.run() }, delay)

            SchedulerType.REGION -> Bukkit.getRegionScheduler().runDelayed(
                plugin, location, { runnable.run() }, delay
            )

            SchedulerType.ASYNC -> Bukkit.getAsyncScheduler()
                .runDelayed(plugin, { runnable.run() }, delay * 50, TimeUnit.MILLISECONDS)
        }!!
    )

    override fun runTaskTimer(runnable: Runnable, delay: Long, period: Long): ProxyTask = ProxyFoliaTask(
        when (schedulerType) {
            SchedulerType.ENTITY -> entity.scheduler.runAtFixedRate(
                plugin, { runnable.run() }, retired, delay, period
            )

            SchedulerType.GLOBAL -> Bukkit.getGlobalRegionScheduler()
                .runAtFixedRate(plugin, { runnable.run() }, delay, period)

            SchedulerType.REGION -> Bukkit.getRegionScheduler().runAtFixedRate(
                plugin, location, { runnable.run() }, delay, period
            )

            SchedulerType.ASYNC -> Bukkit.getAsyncScheduler()
                .runAtFixedRate(plugin, { runnable.run() }, delay * 50, period * 50, TimeUnit.MILLISECONDS)

        }!!
    )

    /**
     * 在Folia中没有Bukkit中"异步"的概念
     * @param runnable 需要执行的程序
     * @return 调度任务实例
     */
    override fun runTaskAsynchronously(runnable: Runnable): ProxyTask {
        return runTask(runnable)
    }

    override fun runTaskLaterAsynchronously(runnable: Runnable, delay: Long): ProxyTask {
        return runTaskLater(runnable, delay)
    }

    override fun runTaskTimerAsynchronously(runnable: Runnable, delay: Long, period: Long): ProxyTask {
        return runTaskTimer(runnable, delay, period)
    }
}