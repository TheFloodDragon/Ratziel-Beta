package cn.fd.ratziel.common.message

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.title.Title
import java.time.Duration

/**
 * 发送消息
 */
fun Audience.sendMessage(message: String) {
    this.sendMessage(Message.buildMessage(message))
}

/**
 * 发送动作栏消息
 */
fun Audience.sendActionBar(message: String) {
    this.sendActionBar(Message.buildMessage(message))
}

/**
 * 发送标题
 */
fun Audience.sendTitle(title: String?, subtitle: String?, fadeIn: Duration, stay: Duration, fadeOut: Duration) {
    this.showTitle(
        Title.title(
            Message.buildMessage(title),
            Message.buildMessage(subtitle),
            Title.Times.times(fadeIn, stay, fadeOut)
        )
    )
}