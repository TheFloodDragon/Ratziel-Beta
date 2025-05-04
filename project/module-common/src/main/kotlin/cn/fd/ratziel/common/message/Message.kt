package cn.fd.ratziel.common.message

import cn.fd.ratziel.common.message.builder.MessageComponentSerializer
import cn.fd.ratziel.common.message.builder.MiniMessageBuilder.TAG_END
import cn.fd.ratziel.common.message.builder.MiniMessageBuilder.TAG_START
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
 * Message
 *
 * @author TheFloodDragon
 * @since 2024/4/4 21:23
 */
object Message {

    /**
     * [MessageWrapper] 服务对象
     */
    @JvmStatic
    val wrapper by lazy { PlatformFactory.getService<MessageWrapper>() }

    /**
     * 基本消息构建
     */
    @JvmStatic
    fun buildMessage(source: String?, vararg tagResolver: TagResolver): Component {
        val text = source ?: return Component.empty()
        val parsed = parseAdventure(text, *tagResolver)
        return parsed
    }

    /**
     * Adventure API消息解析
     */
    @JvmStatic
    fun parseAdventure(source: String, vararg tagResolver: TagResolver): Component =
        wrapper.legacyBuilder.deserialize(translateAmpersandColor(mark(source)))
            .let { wrapper.miniBuilder.serialize(it) }
            .let { wrapper.miniBuilder.deserialize(deMark(it), *tagResolver) }

    /**
     * Taboolib消息解析
     */
    @JvmStatic
    fun parseTaboolibMessage(source: String?): ComponentText =
        source?.let { Components.parseSimple(it).build() } ?: ComponentText.empty()

    /**
     * 将 Json字符串 转换成 [MessageComponent]
     */
    @JvmStatic
    fun transformToJson(component: Component): String = wrapper.gsonBuilder.serialize(component)

    /**
     * 将 [MessageComponent] 转换成 Json字符串
     */
    @JvmStatic
    fun transformFromJson(json: String): Component = wrapper.gsonBuilder.deserialize(json)

    /**
     * 将 '&' 转换成 '§'
     */
    @JvmStatic
    fun translateAmpersandColor(target: String) = target.replace(AMPERSAND_CHAR, SECTION_CHAR)

    /**
     * 将 '§' 转换成 '&'
     */
    @JvmStatic
    fun translateLegacyColor(target: String) = target.replace(SECTION_CHAR, AMPERSAND_CHAR)

    private fun mark(source: String) =
        source.replaceNonEscaped(TAG_START, MARKED_TAG_START).replaceNonEscaped(TAG_END, MARKED_TAG_END)

    private fun deMark(source: String) =
        source.replaceNonEscaped(MARKED_TAG_START, TAG_START).replaceNonEscaped(MARKED_TAG_END, TAG_END)

    const val MARKED_TAG_START = "{marked:start}"
    const val MARKED_TAG_END = "{marked:end}"

}