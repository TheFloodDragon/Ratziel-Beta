package cn.fd.ratziel.bukkit.adventure

import cn.fd.ratziel.common.adventure.buildMessage
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import net.kyori.adventure.title.Title
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.platform.util.bukkitPlugin
import java.time.Duration

val bukkitAudiences by lazy {
    BukkitAudiences.create(bukkitPlugin)
}

val CommandSender.audienceSender
    get() = bukkitAudiences.sender(this)

val Player.audiencePlayer
    get() = bukkitAudiences.player(this)

/**
 * 观众方法扩展
 */
fun Audience.sendActionBar(message: String) {
    this.sendActionBar(buildMessage(message))
}

fun Audience.sendMessage(message: String) {
    this.sendMessage(buildMessage(message))
}

fun Audience.sendTitle(title: String?, subtitle: String?, fadeIn: Duration, stay: Duration, fadeOut: Duration) {
    this.showTitle(Title.title(buildMessage(title), buildMessage(subtitle), Title.Times.times(fadeIn, stay, fadeOut)))
}

fun Audience.sendTitle(title: String?, subtitle: String?, fadeIn: Int, stay: Int, fadeOut: Int) {
    fun ticksToMillis(ticks: Int) = Duration.ofMillis(ticks * 50L)
    this.sendTitle(title, subtitle, ticksToMillis(fadeIn), ticksToMillis(stay), ticksToMillis(fadeOut))
}