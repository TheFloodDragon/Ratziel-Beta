package cn.fd.ratziel.module.item.impl.builder.provided

import cn.fd.ratziel.common.element.registry.AutoRegister
import cn.fd.ratziel.core.contextual.ArgumentContext
import cn.fd.ratziel.core.contextual.AttachedContext
import cn.fd.ratziel.core.serialization.json.JsonTree
import cn.fd.ratziel.module.item.api.builder.ItemSectionResolver
import cn.fd.ratziel.module.item.api.builder.ItemTagResolver
import cn.fd.ratziel.module.script.ScriptManager
import cn.fd.ratziel.module.script.ScriptType
import cn.fd.ratziel.block.provided.ScriptBlock
import taboolib.common.util.VariableReader
import java.util.concurrent.ConcurrentHashMap

/**
 * InlineScriptResolver
 *
 * @author TheFloodDragon
 * @since 2025/6/7 19:08
 */
object InlineScriptResolver : ItemSectionResolver {

    internal val scriptsCatcher = AttachedContext.catcher<MutableMap<String, ScriptBlock>>(this) { ConcurrentHashMap() }

    override fun prepare(node: JsonTree.Node, context: ArgumentContext) {
        val section = node.stringSection() ?: return
        // 创建脚本
        val scripts = analyzeParts(section.value.content).mapNotNull { (content, language) ->
            if (language != null) {
                ScriptBlock(content, language.executor)
            } else null
        }
        // 扔到缓存里
        scriptsCatcher[context].putAll(scripts.associateBy { it.source })
    }

    override fun resolve(node: JsonTree.Node, context: ArgumentContext) {
        val section = node.stringSection() ?: return
        val resolved = analyzeParts(section.value.content).joinToString("") { (content, language) ->
            if (language != null) {
                // 获取脚本
                val script = scriptsCatcher[context].getOrPut(content) {
                    ScriptBlock(content, language.executor)
                }
                // 评估脚本并返回结果
                script.execute(context).toString()
            } else content
        }
        section.value(resolved)
    }

    fun analyzeParts(content: String): List<Pair<String, ScriptType?>> {
        return reader.readToFlatten(content).map {
            if (it.isVariable) {
                val index = it.text.indexOf(':')
                // 获取脚本语言
                var language: ScriptType? = null
                if (index != -1) language = ScriptType.match(it.text.substring(0, index))
                // 读取脚本文本
                val content = it.text.substring(index + 1)
                content to (language ?: ScriptManager.defaultLanguage)
            } else it.text to null
        }
    }

    @AutoRegister
    object InlineScriptTagResolver : ItemTagResolver {
        override val alias = arrayOf("script")

        override fun prepare(args: List<String>, context: ArgumentContext) {
            if (args.isEmpty()) return
            // 匹配脚本语言和内容
            var language: ScriptType? = null
            var content = args.joinToString("")
            // 匹配设置的脚本语言
            if (args.size >= 2) {
                val matched = ScriptType.match(args.first())
                if (matched != null) {
                    language = matched
                    content = args.drop(1).joinToString("")
                }
            }
            // 解析内联脚本
            val script = ScriptBlock(content, (language ?: ScriptManager.defaultLanguage).executor)
            // 放入缓存
            scriptsCatcher[context][content] = script
        }

        override fun resolve(args: List<String>, context: ArgumentContext): String? {
            val content = args.joinToString("")
            // 获取内联脚本 (经过预处理的)
            val script = scriptsCatcher[context][content] ?: return null
            // 评估脚本并返回结果
            return script.execute(context).toString()
        }

    }

    /** 标签读取器 **/
    @JvmStatic
    private val reader = VariableReader("{{", "}}")

}