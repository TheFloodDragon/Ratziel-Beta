package cn.fd.ratziel.common.message

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
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

/**
 * 按照指定字符将 Adventure [Component] 拆分为多段。
 */
fun Component.splitBy(separator: Char): List<Component> {
    fun split(component: Component): List<Component> {
        val segments = when (component) {
            is TextComponent -> buildList {
                val content = component.content()
                var startIndex = 0
                content.forEachIndexed { index, char ->
                    if (char == separator) {
                        add(component.content(content.substring(startIndex, index)).children(emptyList()))
                        startIndex = index + 1
                    }
                }
                add(component.content(content.substring(startIndex)).children(emptyList()))
            }

            else -> listOf(component.children(emptyList()))
        }.toMutableList()

        component.children().forEach { child ->
            val childSegments = split(child)
            segments[segments.lastIndex] = segments.last().append(childSegments.first())
            childSegments.drop(1).forEach { extraSegment ->
                segments += Component.empty().style(component.style()).append(extraSegment)
            }
        }
        return segments
    }

    return split(this)
}

/**
 * 按照换行符将 Adventure [Component] 拆分为多行。
 */
fun Component.splitByNewline(): List<Component> {
    return splitBy('\n')
}
