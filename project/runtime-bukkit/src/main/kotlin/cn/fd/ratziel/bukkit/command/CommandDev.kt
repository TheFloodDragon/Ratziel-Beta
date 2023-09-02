package cn.fd.ratziel.bukkit.command

import cn.fd.ratziel.common.command.CommandElement
import cn.fd.ratziel.core.util.runFuture
import cn.fd.ratziel.kether.KetherHandler
import org.bukkit.command.CommandSender
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.subCommand
import taboolib.expansion.createHelper

/**
 * CommandDev
 *
 * @author TheFloodDragon
 * @since 2023/8/12 11:47
 */
@CommandHeader(
    name = "r-dev",
    aliases = ["rdev"],
    permission = "ratziel.command.dev",
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
        dynamic {
            execute<CommandSender> { sender, _, context ->
                runFuture {
                    KetherHandler.invoke(context, sender, mapOf()).thenApply {
                        sender.sendMessage("§7Result: $it")
                    }
                }
            }
        }
    }

    @CommandBody
    val element = CommandElement

}