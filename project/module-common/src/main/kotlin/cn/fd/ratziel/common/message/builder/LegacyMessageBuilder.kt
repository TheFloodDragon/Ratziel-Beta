package cn.fd.ratziel.common.message.builder

import cn.fd.ratziel.common.message.MessageBuilder
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer

/**
 * LegacyMessageBuilder
 *
 * @author TheFloodDragon
 * @since 2023/12/2 12:42
 */
class LegacyMessageBuilder(
    /**
     * 是否支持十六进制颜色
     */
    val hexColors: Boolean = true,
    /**
     * 采用的颜色字符
     */
    val character: Char = LegacyComponentSerializer.SECTION_CHAR,
    val hexCharacter: Char = LegacyComponentSerializer.HEX_CHAR,
    /**
     * 序列化十六进制颜色时是否使用“&x”重复代码格式
     */
    val useUnusualXRepeatedCharacterHexFormat: Boolean = true,
) : MessageBuilder {

    val serializer by lazy {
        LegacyComponentSerializer.builder().also {
            it.character(this.character)
            it.hexCharacter(this.hexCharacter)
            if (this.hexColors) it.hexColors()
            if (this.useUnusualXRepeatedCharacterHexFormat) it.useUnusualXRepeatedCharacterHexFormat()
        }.build()
    }

    override fun serialize(source: Component): String = serializer.serialize(source)

    override fun deserialize(source: String): Component = serializer.deserialize(source)

}