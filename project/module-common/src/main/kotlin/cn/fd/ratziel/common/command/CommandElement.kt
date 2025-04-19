package cn.fd.ratziel.common.command

import cn.fd.ratziel.common.WorkspaceLoader
import cn.fd.ratziel.common.element.registry.ElementRegistry
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
    name = "r-element",
    permission = "ratziel.command.element",
    description = "Element Command"
)
object CommandElement {

    @CommandBody
    val main = mainCommand { createHelper() }

    /**
     * 列出已加载的所有元素
     */
    @CommandBody
    val list = subCommand {
        executeAsync<ProxyCommandSender> { sender, _, _ ->
            sender.sendLang("Element-Header")
            WorkspaceLoader.currentEvaluator.evaluatedElements.values.forEach {
                sender.sendLang("Element-Identifier-Format", it.name) // 名称
                sender.sendLang(
                    "Element-Info-Format",
                    it.type.toString(), // 类型
                    it.file?.path ?: "None", // 文件
                    it.property.toString() // 属性
                )
            }
        }
    }


    /**
     * 列出所有元素类型
     */
    @CommandBody
    val listType = subCommand {
        executeAsync<ProxyCommandSender> { sender, _, _ ->
            sender.sendLang("Element-Type-Header")
            ElementRegistry.registry.keys.forEach { type ->
                // 命名空间消息
                sender.sendLang("Element-Type-Namespace-Format", type.space)
                // 具体消息
                sender.sendLang(
                    "Element-Type-Info-Format",
                    type.name, // 名称
                    format(type.alias.toList()), // 别名
                    ElementRegistry[type]::class.java.name//处理器
                )
            }
        }
    }

    private fun format(list: Iterable<String>?) =
        list?.joinToString(separator = ", ")?.takeUnless { it.isEmpty() } ?: "None"

}