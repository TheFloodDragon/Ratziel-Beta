package cn.fd.ratziel.common.message

import cn.fd.ratziel.common.message.builder.GsonMessageBuilder
import cn.fd.ratziel.common.message.builder.LegacyMessageBuilder
import cn.fd.ratziel.common.message.builder.MiniMessageBuilder
import taboolib.common.platform.Awake
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import taboolib.module.nms.MinecraftVersion

/**
 * BukkitMessageWrapper
 *
 * @author TheFloodDragon
 * @since 2023/12/2 13:01
 */
@Awake
@PlatformSide([Platform.BUKKIT])
class BukkitMessageWrapper : MessageWrapper {

    private val isLowVersion = MinecraftVersion.isLower(MinecraftVersion.V1_16)

    override val gsonBuilder by lazy {
        GsonMessageBuilder(colorDown = isLowVersion)
    }

    override val legacyBuilder by lazy {
        LegacyMessageBuilder(
            hexColors = !isLowVersion,
            useUnusualXRepeatedCharacterHexFormat = !isLowVersion,
        )
    }

    override val miniBuilder = MiniMessageBuilder

}