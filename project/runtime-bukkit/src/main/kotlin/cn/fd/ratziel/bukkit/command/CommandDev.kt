package cn.fd.ratziel.bukkit.command

import cn.fd.ratziel.common.command.executeAsync
import cn.fd.ratziel.common.message.audienceSender
import cn.fd.ratziel.common.message.sendMessage
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
    permission = "ratziel.command.dev",
    description = "Develop Command"
)
object CommandDev {

    @CommandBody
    val main = mainCommand { createHelper() }

//    /**
//     * 运行Kether
//     */
//    @CommandBody
//    val runKether = subCommand {
//        dynamic {
//            executeAsync<CommandSender> { sender, _, context ->
//                KetherHandler.invoke(context, sender, mapOf()).thenApply {
//                    sender.sendMessage("§7Result: $it")
//                }
//            }
//        }
//    }

    @CommandBody
    val testMessage = subCommand {
        dynamic {
            executeAsync<CommandSender> { sender, _, context ->
                sender.audienceSender.sendMessage(context)
            }
        }
    }

}