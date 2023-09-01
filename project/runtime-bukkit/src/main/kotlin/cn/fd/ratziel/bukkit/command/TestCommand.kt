package cn.fd.ratziel.bukkit.command

import cn.fd.ratziel.common.debug
import cn.fd.ratziel.common.util.runningClassesWithoutTaboolib
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.subCommand

@PlatformSide([Platform.BUKKIT])
@CommandHeader(name = "ftest")
object TestCommand {

    @CommandBody
    val main = mainCommand {
        execute<ProxyCommandSender> { sender, _, _ ->
            debug("Test")
        }
    }

    @CommandBody
    val test = subCommand {
        execute<ProxyCommandSender> { sender, _, _ ->
            debug()
        }
    }

    @CommandBody
    val classes = subCommand {
        execute<ProxyCommandSender> { sender, _, _ ->
            debug(runningClassesWithoutTaboolib)
        }
    }

}