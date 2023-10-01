package cn.fd.ratziel.adventure

import net.kyori.adventure.platform.bukkit.BukkitAudiences
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.platform.util.bukkitPlugin

val bukkitAudiences by lazy {
    BukkitAudiences.create(bukkitPlugin)
}

val CommandSender.audienceSender
    get() = bukkitAudiences.sender(this)

val Player.audiencePlayer
    get() = bukkitAudiences.player(this)