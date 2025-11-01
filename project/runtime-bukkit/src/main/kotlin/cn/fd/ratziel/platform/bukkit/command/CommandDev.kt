package cn.fd.ratziel.platform.bukkit.command

import cn.fd.ratziel.common.message.audienceSender
import cn.fd.ratziel.common.message.sendMessage
import cn.fd.ratziel.common.util.VariablesMap
import cn.fd.ratziel.module.script.ScriptManager
import cn.fd.ratziel.module.script.ScriptService
import cn.fd.ratziel.module.script.api.ScriptContent
import cn.fd.ratziel.module.script.api.ScriptSource
import cn.fd.ratziel.module.script.api.ScriptType
import cn.fd.ratziel.module.script.util.toScriptEnv
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.*
import taboolib.expansion.createHelper
import kotlin.time.measureTimedValue

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
        dynamic("language") {
            suggest {
                ScriptService.enabledLanguages.map { it.languageId }
            }
            dynamic {
                execute<CommandSender> { sender, ctx, content ->
                    val language = ScriptType.match(ctx["language"]) ?: ScriptManager.defaultLanguage
                    // 脚本环境
                    val environment = VariablesMap {
                        put("sender", sender)
                        if (sender is Player) {
                            put("player", sender)
                        }
                    }.toScriptEnv()
                    // 编译脚本
                    val script = if (content.trim().startsWith("-c", ignoreCase = true)) {
                        val source = ScriptSource.literal(content.substringAfter("-c"), language)
                        runCatching {
                            language.executor.build(source, environment)
                        }.getOrElse { sender.sendMessage("Error: " + it.message); it.printStackTrace(); return@execute }
                    } else ScriptContent.literal(content, language)
                    // 运行
                    measureTimedValue {
                        runCatching {
                            language.executor.eval(script, environment)
                        }.getOrElse { sender.sendMessage("Error: " + it.message); it.printStackTrace(); return@execute }
                    }.also {
                        sender.sendMessage("§7Result (${it.duration.inWholeMilliseconds}ms): ${it.value}")
                    }
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