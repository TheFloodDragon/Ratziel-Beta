@file:Suppress("SpellCheckingInspection")

package cn.fd.ratziel.common.util

import taboolib.common.platform.ProxyCommandSender
import taboolib.common.util.VariableReader
import taboolib.common.util.replaceWithOrder
import taboolib.module.chat.ComponentText
import taboolib.module.chat.Components
import taboolib.module.chat.parseToHexColor
import taboolib.module.chat.toGradientColor
import taboolib.module.lang.LanguageFile
import taboolib.module.lang.TypeJson
import taboolib.module.lang.Type as LangType

val bracketParser = VariableReader("[", "]")

/**
 * 获取语言类型
 */
fun LanguageFile.getType(node: String): LangType? = nodes[node]

/**
 * 构建消息
 * @see TypeJson.send
 */
fun TypeJson.asComponent(sender: ProxyCommandSender, vararg args: Any?): ComponentText {
    /** 转换文本 */
    fun String.translated() = translate(sender, args).replaceWithOrder(args)
    /** 构建信息 */
    return Components.empty().apply {
        var i = 0
        text?.forEachIndexed { index, line ->
            // 加载变量
            bracketParser.readToFlatten(line).forEach { part ->
                // 获取文本块类型
                val extra = if (part.isVariable) jsonArgs.getOrNull(i++) else emptyMap()
                if (extra == null) {
                    append("§c[RAW OPTION NOT FOUND]")
                    return@forEach
                }
                // 显示文字
                val showText = part.text.translated()
                val showType = extra["type"].toString().translated()
                when {
                    // 快捷键
                    showType == "keybind" -> appendKeybind(showText)
                    // 选择器
                    showType == "selector" -> appendSelector(showText)
                    // 语言
                    // text: '[commands.drop.success.single]'
                    // args:
                    // - type: translate:1:Stone
                    showType == "translate" -> appendTranslation(
                        showText,
                        *showType.substringAfter(':').split(':').toTypedArray()
                    )
                    // 分数
                    showType == "score" -> appendScore(showText.substringBefore(':'), showText.substringAfter(':'))
                    // 渐变颜色文本
                    // text: 'Woo: [||||||||||||||||||||||||]'
                    // args:
                    // - type: gradient:#ff0000:#00ff00:#0000ff:#ff0000
                    showType.startsWith("gradient") -> {
                        append(
                            showText.toGradientColor(
                                showType.substringAfter(':').split(':').map { it.parseToHexColor() })
                        )
                    }
                    // 标准
                    else -> append(showText)
                }
                // 附加信息
                if (extra.containsKey("hover")) {
                    hoverText(extra["hover"].toString().translated())
                }
                if (extra.containsKey("command")) {
                    clickRunCommand(extra["command"].toString().translated())
                }
                if (extra.containsKey("suggest")) {
                    clickSuggestCommand(extra["suggest"].toString().translated())
                }
                if (extra.containsKey("insertion")) {
                    clickInsertText(extra["insertion"].toString().translated())
                }
                if (extra.containsKey("copy")) {
                    clickCopyToClipboard(extra["copy"].toString().translated())
                }
                if (extra.containsKey("file")) {
                    clickOpenFile(extra["file"].toString().translated())
                }
                if (extra.containsKey("url")) {
                    clickOpenURL(extra["url"].toString().translated())
                }
                if (extra.containsKey("font")) {
                    font(extra["font"].toString().translated())
                }
            }
            if (index + 1 < text!!.size) {
                newLine()
            }
        }
    }
}