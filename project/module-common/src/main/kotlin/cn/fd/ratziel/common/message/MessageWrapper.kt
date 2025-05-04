package cn.fd.ratziel.common.message

import cn.fd.ratziel.common.message.builder.GsonMessageBuilder
import cn.fd.ratziel.common.message.builder.LegacyMessageBuilder
import cn.fd.ratziel.common.message.builder.MiniMessageBuilder
import net.kyori.adventure.platform.AudienceProvider
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
    val audienceProvider: AudienceProvider

    /**
     * 消息构建器 - [LegacyMessageBuilder]
     */
    val legacyBuilder: LegacyMessageBuilder

    /**
     * 消息构建器 - [GsonMessageBuilder]
     */
    val gsonBuilder: GsonMessageBuilder

    /**
     * 消息构建器 - [MiniMessageBuilder]
     */
    val miniBuilder: MiniMessageBuilder

}