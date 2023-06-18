package cn.fd.utilities.bukkit

import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import taboolib.common.platform.Plugin
import taboolib.common.platform.function.console
import taboolib.module.lang.sendLang
import taboolib.platform.BukkitPlugin

@PlatformSide([Platform.BUKKIT])
object FDUtilities : Plugin() {

    val plugin by lazy { BukkitPlugin.getInstance() }

    override fun onLoad() {
        console().sendLang("Plugin-Loading")
    }

    override fun onEnable() {
        console().sendLang("Plugin-Enabled")
    }

}