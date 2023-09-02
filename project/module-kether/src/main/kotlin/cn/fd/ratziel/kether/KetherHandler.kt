package cn.fd.ratziel.kether

import cn.fd.ratziel.common.util.alert
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.function.adaptCommandSender
import taboolib.common.platform.function.adaptPlayer
import taboolib.common.platform.function.console
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

/**
 * KetherHandler
 *
 * @author TheFloodDragon
 * @since 2023/8/9 20:57
 */
object KetherHandler {

    private fun defaultOptions(sender: CommandSender?, vars: Map<String, Any?>) = ScriptOptions.builder()
        .namespace(KetherTransfer.namespace)
        .sender(sender?.let { if (it is Player) adaptPlayer(it) else adaptCommandSender(it) } ?: console())
        .vars(KetherShell.VariableMap(vars))
        .build()

    fun invoke(source: String, sender: CommandSender?, vars: Map<String, Any?>): CompletableFuture<Any?> = alert {
        runKether {
            KetherShell.eval(
                source,
                options = defaultOptions(sender, vars)
            )
        }
    } ?: CompletableFuture.completedFuture(null)

    fun parseInline(source: String, sender: CommandSender?, vars: Map<String, Any?>) = alert {
        KetherFunction.parse(
            source,
            options = defaultOptions(sender, vars)
        )
    } ?: "<E: $source>"

}