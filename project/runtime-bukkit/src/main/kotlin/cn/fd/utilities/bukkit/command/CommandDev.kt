package cn.fd.utilities.bukkit.command

import cn.fd.utilities.common.debug
import cn.fd.utilities.kether.KetherHandler
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.function.adaptCommandSender
import taboolib.expansion.createHelper
import taboolib.module.chat.component
import taboolib.platform.util.onlinePlayers

/**
 * @author Arasple, TheFloodDragon
 * @since 2023/8/12 11:47
 */
@CommandHeader(name = "fdev")
object CommandDev {

    @CommandBody
    val main = mainCommand { createHelper() }

    /**
     * 运行Kether
     */
    @CommandBody
    val runKether = subCommand {
        execute<CommandSender> { sender, _, argument ->
            val player = if (sender is Player) sender else onlinePlayers.random()
            debug(argument)
            val script = argument.removePrefix("runKether ")

            KetherHandler.invoke(script, player, mapOf()).thenApply {
                sender.sendMessage("§7Result: $it")
            }
        }
    }

    @CommandBody
    val testComponent = subCommand {
        execute<CommandSender> { sender, _, argument ->
            val message = argument.split(" ", limit = 2)[1]

            message
                .component()
                .build { colored() }
                .also {
                    it.toLegacyText()
                    println(
                        """
                            Component:: ${it.toLegacyText().replace('§', '&')}
                        """.trimIndent()
                    )
                }
                .sendTo(adaptCommandSender(sender))
        }
    }

}