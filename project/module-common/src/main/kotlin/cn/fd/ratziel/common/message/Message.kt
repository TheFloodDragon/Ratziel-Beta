package cn.fd.ratziel.common.message

import cn.fd.ratziel.common.message.builder.MessageComponentSerializer
import cn.fd.ratziel.common.message.builder.MiniMessageBuilder.TAG_END
import cn.fd.ratziel.common.message.builder.MiniMessageBuilder.TAG_START
import cn.fd.ratziel.core.serialization.isJson
import cn.fd.ratziel.core.util.replaceNonEscaped
import kotlinx.serialization.Serializable
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.AMPERSAND_CHAR
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.SECTION_CHAR
import taboolib.common.platform.PlatformFactory
import taboolib.module.chat.ComponentText
import taboolib.module.chat.Components

typealias MessageComponent = @Serializable(MessageComponentSerializer::class) Component

/**
 * Json - 组件 互转
 */
fun Component.toJsonString(): String = Message.wrapper.gsonBuilder.serialize(this)

fun componentFromJson(json: String): Component = Message.wrapper.gsonBuilder.deserialize(json)

/**
 * 基本消息构建
 */
fun buildMessage(source: String?, vararg tagResolver: TagResolver): Component = Message.build(source, *tagResolver)

object Message {
    /**
     * 基本消息构建
     */
    @JvmStatic
    fun build(source: String?, vararg tagResolver: TagResolver): Component = source?.let {
        if (it.isJson()) componentFromJson(it)
        else parseAdventure(it, *tagResolver)
    } ?: Component.empty()

    /**
     * MessageWrapper 对象
     */
    @JvmStatic
    val wrapper by lazy { PlatformFactory.getService<MessageWrapper>() }

    /**
     * Taboolib消息解析
     */
    @JvmStatic
    fun parseTaboolibMessage(source: String?): ComponentText =
        source?.let { Components.parseSimple(it).build() } ?: ComponentText.empty()

    /**
     * 冒险API消息解析
     */
    @JvmStatic
    fun parseAdventure(source: String, vararg tagResolver: TagResolver): Component =
        wrapper.legacyBuilder.deserialize(translateAmpersandColor(mark(source)))
            .let { wrapper.miniBuilder.serialize(it) }
            .let { wrapper.miniBuilder.deserialize(deMark(it), *tagResolver) }

    private fun mark(source: String) =
        source.replaceNonEscaped(TAG_START, MARKED_TAG_START).replaceNonEscaped(TAG_END, MARKED_TAG_END)

    private fun deMark(source: String) =
        source.replaceNonEscaped(MARKED_TAG_START, TAG_START).replaceNonEscaped(MARKED_TAG_END, TAG_END)

    const val MARKED_TAG_START = "{marked:start}"
    const val MARKED_TAG_END = "{marked:end}"

}

/**
 * 将 '&' 转换成 '§'
 */
fun translateAmpersandColor(target: String) = target.replace(AMPERSAND_CHAR, SECTION_CHAR)

/**
 * 将 '§' 转换成 '&'
 */
fun translateLegacyColor(target: String) = target.replace(SECTION_CHAR, AMPERSAND_CHAR)