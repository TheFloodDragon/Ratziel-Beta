package cn.fd.utilities.common.command

import cn.fd.utilities.core.element.type.ElementService
import cn.fd.utilities.core.util.runFuture
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
 * @author TheFloodDragon
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
            runFuture {
                sender.sendLang("Element-Type-Header")
                ElementService.getRegistry().forEach { etype, handlers ->
                    // 命名空间消息
                    sender.sendLang("Element-Type-Namespace-Format", etype.space)
                    // 具体消息
                    sender.sendLang(
                        "Element-Type-Info-Format",
                        etype.name, // 名称
                        format(etype.getAlias()), // 别名
                        format(handlers.map { it::class.java.name }) //处理器
                    )
                }
            }
        }
    }

    private fun format(set: Set<*>?): String {
        return format(set?.toList())
    }

    private fun format(list: List<*>?): String {
        return list.toString().let { it.subSequence(1, it.lastIndex).replace(Regex("\\s"), "") }
    }

}