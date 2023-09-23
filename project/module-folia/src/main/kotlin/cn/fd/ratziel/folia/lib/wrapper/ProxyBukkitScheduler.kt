package cn.fd.ratziel.folia.lib.wrapper

import cn.fd.ratziel.folia.lib.ProxyScheduler
import cn.fd.ratziel.folia.lib.ProxyTask
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin

/**
 * ProxyBukkitScheduler
 *
 * @author TheFloodDragon
 * @since 2023/9/23 12:18
 */
class ProxyBukkitScheduler(val plugin: Plugin) : ProxyScheduler {

    override fun runTask(runnable: Runnable): ProxyTask {
        return ProxyBukkitTask(Bukkit.getScheduler().runTask(plugin, runnable), false)
    }

    override fun runTaskLater(runnable: Runnable, delay: Long): ProxyTask {
        return ProxyBukkitTask(Bukkit.getScheduler().runTaskLater(plugin, runnable, delay), false)
    }

    override fun runTaskTimer(runnable: Runnable, delay: Long, period: Long): ProxyTask {
        return ProxyBukkitTask(Bukkit.getScheduler().runTaskTimer(plugin, runnable, delay, period), true)
    }

    override fun runTaskAsynchronously(runnable: Runnable): ProxyTask {
        return ProxyBukkitTask(Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable), false)
    }

    override fun runTaskLaterAsynchronously(runnable: Runnable, delay: Long): ProxyTask {
        return ProxyBukkitTask(Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, runnable, delay), false)
    }

    override fun runTaskTimerAsynchronously(runnable: Runnable, delay: Long, period: Long): ProxyTask {
        return ProxyBukkitTask(Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, runnable, delay, period), true)
    }
}