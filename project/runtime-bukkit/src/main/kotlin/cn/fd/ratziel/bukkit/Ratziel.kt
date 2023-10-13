package cn.fd.ratziel.bukkit

import taboolib.common.platform.Plugin
import taboolib.common.platform.function.console
import taboolib.common.platform.function.pluginVersion
import taboolib.module.lang.sendLang
import taboolib.platform.BukkitPlugin

object Ratziel : Plugin() {

    val plugin by lazy { BukkitPlugin.getInstance() }

    override fun onLoad() {
        println("Test: @kotlin_version@")
        console().sendLang("Plugin-Loading", pluginVersion)
    }

    override fun onEnable() {
        console().sendLang("Plugin-Enabled", pluginVersion)
    }

}