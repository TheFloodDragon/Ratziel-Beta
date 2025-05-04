package cn.fd.ratziel.common.message

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.ProxyPlayer

val ProxyPlayer.castBukkit
    get() = this.cast<Player>()

val CommandSender.audienceSender
    get() = BukkitMessage.audienceProvider.sender(this)

val Player.audiencePlayer
    get() = BukkitMessage.audienceProvider.player(this)

val ProxyPlayer.castAudience
    get() = this.castBukkit.audiencePlayer

val ProxyCommandSender.castBukkit
    get() = this.cast<CommandSender>()

val ProxyCommandSender.castAudience
    get() = this.castBukkit.audienceSender
