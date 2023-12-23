@file:Suppress("UnstableApiUsage")

package cn.fd.ratziel.common.message.builder

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.internal.parser.TokenParser
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver

/**
 * MiniMessageBuilder - 小消息构建
 * [MiniMessage](https://docs.advntr.dev/minimessage/index.html)
 *
 * @author TheFloodDragon
 * @since 2023/12/2 12:25
 */
object MiniMessageBuilder : MessageBuilder {

    const val TAG_START = TokenParser.TAG_START.toString()
    const val TAG_END = TokenParser.TAG_END.toString()

    override fun serialize(source: Component): String = MiniMessage.miniMessage().serialize(source)

    fun deserialize(source: String, vararg tagResolver: TagResolver): Component =
        MiniMessage.miniMessage().deserialize(source, *tagResolver)

    override fun deserialize(source: String): Component = deserialize(source)

}