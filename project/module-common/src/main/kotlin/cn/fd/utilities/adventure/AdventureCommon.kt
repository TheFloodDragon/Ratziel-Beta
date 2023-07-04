package cn.fd.utilities.adventure

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import taboolib.module.nms.MinecraftVersion

/**
 * Invero
 * cc.trixey.invero.common.adventure.AdventureKT
 *
 * @author Arasple
 * @since 2023/3/9 8:06
 */
private const val SECTION_CHAR = '§'

private const val AMPERSAND_CHAR = '&'

private val legacyComponentSerializer by lazy {
    LegacyComponentSerializer.builder().apply {
        if (MinecraftVersion.majorLegacy >= 11600) {
            hexColors()
            useUnusualXRepeatedCharacterHexFormat()
        }
    }.build()
}

fun String.parseMiniMessage() =
    legacyComponentSerializer.serialize(
        parseMiniMessageComponent(translateLegacyColor())
    )

fun parseMiniMessageComponent(string: String): Component {
    return MiniMessage.miniMessage().deserialize(string)
}

fun String.translateAmpersandColor(): String {
    return replace(AMPERSAND_CHAR, SECTION_CHAR)
}

fun String.translateLegacyColor(): String {
    return replace(SECTION_CHAR, AMPERSAND_CHAR)
}