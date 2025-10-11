package cn.fd.ratziel.platform.bukkit

import cn.fd.ratziel.common.command.CommandMain
import cn.fd.ratziel.platform.bukkit.command.CommandDev
import taboolib.common.platform.Plugin
import taboolib.common.platform.function.console
import taboolib.common.platform.function.pluginVersion
import taboolib.module.lang.sendLang
import taboolib.platform.BukkitMetrics
import taboolib.platform.BukkitPlugin

object Ratziel : Plugin() {

    override fun onLoad() {
        console().sendLang("Plugin-Loading", pluginVersion)
    }

    override fun onEnable() {
        // 子命令注册
        CommandMain.registerSubCommand(CommandDev::class.java, "dev")
        // 启动插件统计
        runCatching {
            BukkitMetrics(BukkitPlugin.getInstance(), "Ratziel", 24631, pluginVersion)
        }
        console().sendLang("Plugin-Enabled", pluginVersion)
    }

}