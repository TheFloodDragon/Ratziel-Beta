package cn.fd.ratziel.common.message

import net.kyori.adventure.platform.AudienceProvider
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import taboolib.common.platform.PlatformService

/**
 * MessageWrapper
 *
 * @author TheFloodDragon
 * @since 2023/12/2 12:51
 */
@PlatformService
interface MessageWrapper {

    /**
     * 观众提供者
     */
    val audiences: AudienceProvider

    /**
     * 消息构建器 - [LegacyComponentSerializer]
     */
    val legacyBuilder: LegacyComponentSerializer

    /**
     * 消息构建器 - [GsonComponentSerializer]
     * [GsonMessage](https://docs.advntr.dev/serializer/gson.html)
     */
    val gsonBuilder: GsonComponentSerializer

    /**
     * 消息构建器 - [MiniMessage]
     * [MiniMessage](https://docs.advntr.dev/minimessage/index.html)
     */
    val miniBuilder: MiniMessage

}