package cn.fd.ratziel.bukkit.command

import cn.fd.ratziel.common.WorkspaceLoader
import cn.fd.ratziel.common.command.CommandElement
import cn.fd.ratziel.common.command.executeAsync
import cn.fd.ratziel.common.config.Settings
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
    description = "Main Command"
)
object Command {

    @CommandBody
    val main = mainCommand { createHelper() }

    @CommandBody
    val dev = CommandDev

    @CommandBody
    val element = CommandElement

    @CommandBody
    val reload = subCommand {
        executeAsync<ProxyCommandSender> { sender, _, _ ->
            measureTimeMillis {
                /**
                 * 重载配置
                 */
                Settings.conf.reload()
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
