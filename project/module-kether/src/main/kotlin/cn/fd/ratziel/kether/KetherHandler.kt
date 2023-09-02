package cn.fd.ratziel.kether

import cn.fd.ratziel.common.loader.alert
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.function.*
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

/**
 * KetherHandler
 *
 * @author TheFloodDragon
 * @since 2023/8/9 20:57
 */
object KetherHandler {

    private fun defaultOptions(sender: ProxyCommandSender?, vars: Map<String, Any?>) = ScriptOptions.builder()
        .namespace(KetherTransfer.namespace)
        .sender(sender?.let { adaptCommandSender(it) } ?: console())
        .vars(KetherShell.VariableMap(vars))
        .build()

    fun invoke(source: String, sender: ProxyCommandSender, vars: Map<String, Any?>): CompletableFuture<Any?> = alert {
        runKether {
            KetherShell.eval(
                source,
                options = defaultOptions(sender, vars)
            )
        }
    } ?: CompletableFuture.completedFuture(null)

    fun parseInline(source: String, sender: ProxyCommandSender?, vars: Map<String, Any?>) = alert {
        KetherFunction.parse(
            source,
            options = defaultOptions(sender, vars)
        )
    } ?: "<E: $source>"

}