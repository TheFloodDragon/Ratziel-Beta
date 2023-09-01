package cn.fd.ratziel.bukkit.command

import cn.fd.ratziel.kether.KetherHandler
import cn.fd.ratziel.common.command.CommandElement
import cn.fd.ratziel.common.debug
import cn.fd.ratziel.core.util.runFuture
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.subCommand
import taboolib.expansion.createHelper
import taboolib.platform.util.onlinePlayers

/**
 * @author Arasple, TheFloodDragon
 * @since 2023/8/12 11:47
 */
@CommandHeader(
    name = "f-dev",
    aliases = ["fdev"],
    permission = "fdutilities.command.dev",
    description = "开发命令"
)
object CommandDev {

    @CommandBody
    val main = mainCommand { createHelper() }

    /**
     * 运行Kether
     */
    @CommandBody
    val runKether = subCommand {
        execute<CommandSender> { sender, _, argument ->
            runFuture {
                val player = if (sender is Player) sender else onlinePlayers.random()
                debug(argument)
                val script = argument.removePrefix("runKether ")

                KetherHandler.invoke(script, player, mapOf()).thenApply {
                    sender.sendMessage("§7Result: $it")
                }
            }
        }
    }

    @CommandBody
    val element = CommandElement

}