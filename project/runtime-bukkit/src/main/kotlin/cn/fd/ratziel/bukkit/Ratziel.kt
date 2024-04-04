package cn.fd.ratziel.bukkit

import taboolib.common.platform.Plugin
import taboolib.common.platform.function.console
import taboolib.common.platform.function.pluginVersion
import taboolib.module.lang.sendLang

object Ratziel : Plugin() {

    override fun onLoad() {
        console().sendLang("Plugin-Loading", pluginVersion)
    }

    override fun onEnable() {
        console().sendLang("Plugin-Enabled", pluginVersion)
    }

}