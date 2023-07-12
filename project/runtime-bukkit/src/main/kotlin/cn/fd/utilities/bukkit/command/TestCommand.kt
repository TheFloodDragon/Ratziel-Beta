package cn.fd.utilities.bukkit.command

import cn.fd.utilities.common.debug
import cn.fd.utilities.util.runningClassesWithoutTaboolib
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.subCommand

@PlatformSide([Platform.BUKKIT])
@CommandHeader(name = "fdutilities")
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