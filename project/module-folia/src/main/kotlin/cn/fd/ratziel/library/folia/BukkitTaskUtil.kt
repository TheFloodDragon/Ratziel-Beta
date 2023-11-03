package cn.fd.ratziel.library.folia

import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import taboolib.library.reflex.Reflex.Companion.getProperty

fun BukkitRunnable.runTimerAsyncQuick(delay: Long, period: Long) = Folia.runTimerAsync(this, delay, period)

fun BukkitRunnable.runTimerQuick(delay: Long, period: Long) = Folia.runTimer(this, delay, period)

fun BukkitRunnable.runLaterAsyncQuick(delay: Long) = Folia.runLaterAsync(this, delay)

fun BukkitRunnable.runLaterQuick(delay: Long) = Folia.runLater(this, delay)

fun BukkitRunnable.runAsyncQuick() = Folia.runAsync { this.run() }

fun BukkitRunnable.runTaskQuick() = Folia.runTask(this)

fun BukkitRunnable.cancelQuick() = getProperty<BukkitTask>("task")?.cancel()