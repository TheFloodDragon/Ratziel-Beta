package cn.fd.ratziel.bukkit.util

import cn.fd.ratziel.adventure.*
import cn.fd.ratziel.bukkit.adventure.audiencePlayer
import cn.fd.ratziel.bukkit.adventure.audienceSender
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.ProxyPlayer

val ProxyPlayer.castBukkit
    get() = this.cast<Player>()

val ProxyPlayer.castAudience
    get() = this.castBukkit.audiencePlayer

val ProxyCommandSender.castBukkit
    get() = this.cast<CommandSender>()

val ProxyCommandSender.castAudience
    get() = this.castBukkit.audienceSender