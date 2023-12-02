package cn.fd.ratziel.common.message.builder

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer

/**
 * GsonMessageBuilder - Gson消息构建
 * [GsonMessage](https://docs.advntr.dev/serializer/gson.html)
 *
 * @author TheFloodDragon
 * @since 2023/12/2 12:37
 */
class GsonMessageBuilder(val colorDown: Boolean = false) : MessageBuilder {

    val serializer by lazy {
        if (colorDown) GsonComponentSerializer.colorDownsamplingGson()
        else GsonComponentSerializer.gson()
    }

    override fun serialize(source: Component): String = serializer.serialize(source)

    fun serializeToTree(source: Component) = serializer.serializeToTree(source)

    override fun deserialize(source: String): Component = serializer.deserialize(source)

}