package cn.fd.ratziel.common.message

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

val CommandSender.audienceSender
    get() = BukkitMessage.audiences.sender(this)

val Player.audiencePlayer
    get() = BukkitMessage.audiences.player(this)


/**
 * 发送消息
 */
fun Player.sendAdventure(message: String) {
    audiencePlayer.sendMessage(Message.buildMessage(message))
}
