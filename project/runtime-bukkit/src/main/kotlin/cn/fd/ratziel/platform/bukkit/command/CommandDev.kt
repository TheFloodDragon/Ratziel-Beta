package cn.fd.ratziel.platform.bukkit.command

import cn.fd.ratziel.common.message.audienceSender
import cn.fd.ratziel.common.message.sendMessage
import cn.fd.ratziel.module.script.ScriptManager
import cn.fd.ratziel.module.script.impl.VariablesMap
import cn.fd.ratziel.module.script.util.eval
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
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

    @CommandBody
    val packet = CommandPacket

    /**
     * 运行默认脚本
     */
    @CommandBody
    val runScript = subCommand {
        dynamic {
            execute<CommandSender> { sender, _, content ->
                val executor = ScriptManager.defaultLanguage.executor
                executor.eval(content, VariablesMap {
                    put("sender", sender)
                    if (sender is Player) {
                        put("player", sender)
                    }
                }).also { result ->
                    sender.sendMessage("§7Result: $result")
                }
            }
        }
    }

    @CommandBody
    val testMessage = subCommand {
        dynamic {
            execute<CommandSender> { sender, _, context ->
                sender.audienceSender.sendMessage(context)
            }
        }
    }

}