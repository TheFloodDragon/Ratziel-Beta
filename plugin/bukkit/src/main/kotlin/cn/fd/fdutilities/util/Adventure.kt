package cn.fd.fdutilities.util

import cn.fd.fdutilities.FDUtilities
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide

@PlatformSide([Platform.BUKKIT])
object Adventure {

    lateinit var Audiences: BukkitAudiences
        private set

    fun enable() {
        Audiences = BukkitAudiences.create(FDUtilities.plugin)
    }

    fun disable() {
        Audiences.close()
    }
}