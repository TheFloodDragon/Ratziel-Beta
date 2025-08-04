package cn.fd.ratziel.common.message

import net.kyori.adventure.platform.bukkit.BukkitAudiences
import net.kyori.adventure.platform.bukkit.BukkitComponentSerializer
import net.kyori.adventure.text.minimessage.MiniMessage
import taboolib.common.platform.Awake
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import taboolib.platform.BukkitPlugin

/**
 * BukkitMessage
 *
 * @author TheFloodDragon
 * @since 2023/12/2 13:01
 */
@Awake
@PlatformSide(Platform.BUKKIT)
object BukkitMessage : MessageWrapper {

    override val audiences by lazy { BukkitAudiences.create(BukkitPlugin.getInstance()) }

    override val gsonBuilder = BukkitComponentSerializer.gson()

    override val legacyBuilder = BukkitComponentSerializer.legacy()

    override val miniBuilder = MiniMessage.miniMessage()

}