package cn.fd.utilities.adventure

import net.kyori.adventure.platform.bukkit.BukkitAudiences
import org.bukkit.entity.Player
import taboolib.platform.util.bukkitPlugin


private val bukkitAudiences by lazy {
    BukkitAudiences.create(bukkitPlugin)
}

fun String.parseMiniMessageAndSend(player: Player) =
    bukkitAudiences
        .player(player)
        .sendMessage(parseMiniMessageComponent(translateLegacyColor()))