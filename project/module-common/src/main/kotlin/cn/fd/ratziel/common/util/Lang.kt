@file:Suppress("SpellCheckingInspection")

package cn.fd.ratziel.common.util

import taboolib.common.platform.ProxyCommandSender
import taboolib.module.lang.LanguageFile
import taboolib.module.lang.TypeJson
import taboolib.module.lang.Type as LangType

/**
 * 获取语言类型
 */
fun LanguageFile.getType(node: String): LangType? = nodes[node]

/**
 * 构建消息
 * @see TypeJson.buildMessage
 */
fun TypeJson.asComponent(sender: ProxyCommandSender, vararg args: Any) = this.buildMessage(sender, *args)