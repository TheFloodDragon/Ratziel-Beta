package cn.fd.ratziel.bukkit.command

import cn.fd.ratziel.common.WorkspaceLoader
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.subCommand
import taboolib.expansion.createHelper
import taboolib.module.lang.Language
import taboolib.module.lang.sendLang
import kotlin.system.measureTimeMillis

@CommandHeader(
    name = "ratziel",
    aliases = ["r", "rz", "f"],
    permission = "ratziel.command.main",
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
            measureTimeMillis {
                /**
                 * 重载语言
                 */
                Language.reload()
                /**
                 * 重载工作空间
                 */
                WorkspaceLoader.reload(sender)
            }.let {
                sender.sendLang("Plugin-Reloaded", it)
            }
        }
    }

}
