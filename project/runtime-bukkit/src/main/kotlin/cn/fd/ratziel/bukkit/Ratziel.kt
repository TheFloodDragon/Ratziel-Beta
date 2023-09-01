package cn.fd.ratziel.bukkit

import kotlinx.serialization.json.Json
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import taboolib.common.platform.Plugin
import taboolib.common.platform.function.console
import taboolib.common.platform.function.pluginVersion
import taboolib.module.lang.sendLang
import taboolib.platform.BukkitPlugin

@PlatformSide([Platform.BUKKIT])
object Ratziel : Plugin() {

    val plugin by lazy { BukkitPlugin.getInstance() }

    override fun onLoad() {
        console().sendLang("Plugin-Loading", pluginVersion)
    }

    override fun onEnable() {
        console().sendLang("Plugin-Enabled", pluginVersion)
    }

}