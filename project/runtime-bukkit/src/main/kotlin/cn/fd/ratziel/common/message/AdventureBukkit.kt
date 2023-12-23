package cn.fd.ratziel.common.message

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import net.kyori.adventure.title.Title
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.ProxyPlayer
import taboolib.platform.BukkitPlugin
import java.time.Duration

val bukkitAudiences by lazy {
    BukkitAudiences.create(BukkitPlugin.getInstance())
}

val ProxyPlayer.castBukkit
    get() = this.cast<Player>()

val CommandSender.audienceSender
    get() = bukkitAudiences.sender(this)

val Player.audiencePlayer
    get() = bukkitAudiences.player(this)

val ProxyPlayer.castAudience
    get() = this.castBukkit.audiencePlayer

val ProxyCommandSender.castBukkit
    get() = this.cast<CommandSender>()

val ProxyCommandSender.castAudience
    get() = this.castBukkit.audienceSender

/**
 * 方法扩展
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