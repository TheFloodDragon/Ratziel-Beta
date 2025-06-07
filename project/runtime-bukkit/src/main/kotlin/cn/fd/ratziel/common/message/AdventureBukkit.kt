package cn.fd.ratziel.common.message

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

val CommandSender.audienceSender
    get() = BukkitMessage.audienceProvider.sender(this)

val Player.audiencePlayer
    get() = BukkitMessage.audienceProvider.player(this)


/**
 * 发送消息
 */
fun Player.tell(message: String) {
    audiencePlayer.sendMessage(Message.buildMessage(message))
}
