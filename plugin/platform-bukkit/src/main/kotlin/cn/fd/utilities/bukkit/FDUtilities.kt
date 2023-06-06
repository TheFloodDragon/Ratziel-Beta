package cn.fd.utilities.bukkit

import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import taboolib.common.platform.Plugin
import taboolib.platform.BukkitPlugin

@PlatformSide([Platform.BUKKIT])
object FDUtilities : Plugin() {

    val plugin by lazy { BukkitPlugin.getInstance() }

    override fun onLoad() {
        println("Hello World!")
    }

}