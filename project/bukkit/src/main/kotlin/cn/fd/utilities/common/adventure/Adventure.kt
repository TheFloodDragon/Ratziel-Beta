package cn.fd.utilities.common.adventure

import net.kyori.adventure.platform.bukkit.BukkitAudiences
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.entity.Player
import taboolib.module.nms.MinecraftVersion
import taboolib.platform.util.bukkitPlugin

/**
 * Invero
 * cc.trixey.invero.common.adventure.AdventureKT
 *
 * @author Arasple
 * @since 2023/3/9 8:06
 */
private const val SECTION_CHAR = '¡ì'

private const val AMPERSAND_CHAR = '&'

private val bukkitAudiences by lazy {
    BukkitAudiences.create(bukkitPlugin)
}

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

fun String.parseMiniMessageAndSend(player: Player) =
    bukkitAudiences
        .player(player)
        .sendMessage(parseMiniMessageComponent(translateLegacyColor()))

private fun parseMiniMessageComponent(string: String): Component {
    return MiniMessage.miniMessage().deserialize(string)
}

fun String.translateAmpersandColor(): String {
    return replace(AMPERSAND_CHAR, SECTION_CHAR)
}

fun String.translateLegacyColor(): String {
    return replace(SECTION_CHAR, AMPERSAND_CHAR)
}