package cn.fd.utilities.common.command

import cn.fd.utilities.core.element.ElementService
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.subCommand
import taboolib.expansion.createHelper
import taboolib.module.lang.sendLang

/**
 * CommandElement
 *
 * @author: TheFloodDragon
 * @since 2023/8/15 11:20
 */
@CommandHeader(
    name = "f-element",
    aliases = ["felement", "element", "em", "fem"],
    permission = "fdutilities.command.element",
)
object CommandElement {

    @CommandBody
    val main = mainCommand { createHelper() }

    /**
     * 列出所有元素类型
     */
    @CommandBody
    val listTypes = subCommand {
        execute<ProxyCommandSender> { sender, _, _ ->
            sender.sendLang("Element-Type-Header")
            ElementService.getRegistry().forEach { pair ->
                sender.sendLang("Element-Type-Namespace-Format", pair.key)
                pair.value.forEach {
                    sender.sendLang(
                        "Element-Type-Info-Format",
                        format(it.names),
                        format(it.handlers.map { h -> h::class.java.name })
                    )
                }
            }
        }
    }

    private fun format(array: Array<*>): String {
        return format(array.toList())
    }

    private fun format(list: List<*>): String {
        return list.toString().let { it.subSequence(1, it.lastIndex).replace(Regex("\\s"), "") }
    }

}