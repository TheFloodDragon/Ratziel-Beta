package cn.fd.ratziel.platform.bukkit

import taboolib.common.platform.Plugin
import taboolib.common.platform.function.console
import taboolib.common.platform.function.pluginVersion
import taboolib.module.lang.sendLang
import taboolib.platform.BukkitMetrics
import taboolib.platform.BukkitPlugin

object Ratziel : Plugin() {

    override fun onLoad() {
        console().sendLang("Plugin-Loading", pluginVersion)
        // 启动插件统计
        runCatching {
            BukkitMetrics(BukkitPlugin.getInstance(), "Ratziel", 24631, pluginVersion)
        }
    }

    override fun onEnable() {
        console().sendLang("Plugin-Enabled", pluginVersion)
    }

}