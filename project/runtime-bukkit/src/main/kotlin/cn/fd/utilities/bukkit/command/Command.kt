package cn.fd.utilities.bukkit.command

import cn.fd.utilities.common.loader.WorkspaceLoader
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.subCommand
import taboolib.expansion.createHelper
import taboolib.module.lang.sendLang
import kotlin.system.measureNanoTime

@CommandHeader(
    name = "fdutilities",
    aliases = ["f", "fd", "fdu"],
    permission = "fdutilities.command.access",
    description = "插件主命令"
)
object Command {

    @CommandBody
    val main = mainCommand { createHelper() }

    @CommandBody
    val dev = CommandDev

    @CommandBody
    val reload = subCommand {
        execute<ProxyCommandSender> { sender, _, _ ->
            val elapsed = measureNanoTime {
                /**
                 * 重载函数内容
                 */
                WorkspaceLoader.reload(sender)
            }
            sender.sendLang("Plugin-Reloaded", elapsed)
        }
    }

}