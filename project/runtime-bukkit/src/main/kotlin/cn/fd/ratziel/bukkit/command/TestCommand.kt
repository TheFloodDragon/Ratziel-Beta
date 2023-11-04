package cn.fd.ratziel.bukkit.command

import cn.fd.ratziel.common.util.runningClassesWithoutTaboolib
import cn.fd.ratziel.kether.bacikal.toBacikalQuest
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.function.info
import taboolib.module.kether.printKetherErrorMessage
import taboolib.platform.type.BukkitPlayer

@PlatformSide([Platform.BUKKIT])
@CommandHeader(name = "ftest")
object TestCommand {

    @CommandBody
    val eval = subCommand {
        dynamic {
            execute<ProxyCommandSender> { sender, _, content ->
                try {
                    val quest = content.toBacikalQuest("vulpecula-eval")
                    info("quest ${quest.name} is built successfully.")

                    quest.runActions {
                        this.sender = sender
                        if (sender is BukkitPlayer) {
                            setVariable("player", sender.player)
                            setVariable("hand", sender.player.equipment?.itemInMainHand)
                        }
                    }.thenAccept {
                        sender.sendMessage(" §5§l‹ ›§r §7Result: §f$it")
                    }
                } catch (e: Exception) {
                    e.printKetherErrorMessage()
                }
            }
        }
    }

    @CommandBody
    val main = mainCommand {
        execute<ProxyCommandSender> { sender, _, _ ->
            println("Test")
        }
    }

    @CommandBody
    val test = subCommand {
        execute<ProxyCommandSender> { sender, _, _ ->
            println()
        }
    }

    @CommandBody
    val classes = subCommand {
        execute<ProxyCommandSender> { sender, _, _ ->
            println(runningClassesWithoutTaboolib)
        }
    }

}