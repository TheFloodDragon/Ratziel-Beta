package cn.fd.utilities.bukkit

import kotlinx.serialization.json.Json
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import taboolib.common.platform.Plugin
import taboolib.common.platform.function.console
import taboolib.common.platform.function.pluginVersion
import taboolib.module.lang.sendLang
import taboolib.platform.BukkitPlugin

@PlatformSide([Platform.BUKKIT])
object FDUtilities : Plugin() {

    val plugin by lazy { BukkitPlugin.getInstance() }

    override fun onLoad() {
        println(
            Json.parseToJsonElement("""
            "{}"
        """.trimIndent()))
        console().sendLang("Plugin-Loading", pluginVersion)
    }

    override fun onEnable() {
        console().sendLang("Plugin-Enabled", pluginVersion)
    }

}