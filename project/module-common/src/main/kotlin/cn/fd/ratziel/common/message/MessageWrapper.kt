package cn.fd.ratziel.common.message

import cn.fd.ratziel.common.message.builder.GsonMessageBuilder
import cn.fd.ratziel.common.message.builder.LegacyMessageBuilder
import cn.fd.ratziel.common.message.builder.MiniMessageBuilder
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
     * 消息构建器
     */
    val legacyBuilder: LegacyMessageBuilder
    val gsonBuilder: GsonMessageBuilder
    val miniBuilder: MiniMessageBuilder

}