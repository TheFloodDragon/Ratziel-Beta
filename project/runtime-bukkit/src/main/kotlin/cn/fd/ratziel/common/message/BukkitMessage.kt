package cn.fd.ratziel.common.message

import cn.fd.ratziel.common.message.builder.GsonMessageBuilder
import cn.fd.ratziel.common.message.builder.LegacyMessageBuilder
import cn.fd.ratziel.common.message.builder.MiniMessageBuilder
import taboolib.common.platform.Awake
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import taboolib.module.nms.MinecraftVersion

/**
 * BukkitMessage
 *
 * @author TheFloodDragon
 * @since 2023/12/2 13:01
 */
@Awake
@PlatformSide(Platform.BUKKIT)
object BukkitMessage : MessageWrapper {

    fun isLegacy() = MinecraftVersion.isLower(MinecraftVersion.V1_16)

    override val gsonBuilder = GsonMessageBuilder(colorDown = isLegacy())

    override val legacyBuilder = LegacyMessageBuilder(hexColors = !isLegacy(), useUnusualXRepeatedCharacterHexFormat = !isLegacy())

    override val miniBuilder = MiniMessageBuilder

}