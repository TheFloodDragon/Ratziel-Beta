package cn.fd.ratziel.module.item.impl.builder.provided

import cn.fd.ratziel.common.element.registry.AutoRegister
import cn.fd.ratziel.core.functional.ArgumentContext
import cn.fd.ratziel.core.functional.CacheContext
import cn.fd.ratziel.core.serialization.json.JsonTree
import cn.fd.ratziel.module.item.api.builder.ItemSectionResolver
import cn.fd.ratziel.module.item.api.builder.ItemTagResolver
import cn.fd.ratziel.module.item.internal.ScriptPart
import cn.fd.ratziel.module.script.ScriptManager
import cn.fd.ratziel.module.script.ScriptType
import cn.fd.ratziel.module.script.api.ScriptContent
import cn.fd.ratziel.module.script.impl.LiteralScriptContent
import cn.fd.ratziel.module.script.impl.SimpleScriptEnvironment
import cn.fd.ratziel.module.script.util.scriptEnv
import cn.fd.ratziel.module.script.util.varsMap
import pers.neige.neigeitems.utils.StringUtils.joinToString
import taboolib.common.util.VariableReader
import java.util.concurrent.ConcurrentHashMap

/**
 * InlineScriptResolver
 *
 * @author TheFloodDragon
 * @since 2025/6/7 19:08
 */
object InlineScriptResolver : ItemSectionResolver {

    internal val scripsCatcher = CacheContext.Catcher<MutableMap<ScriptPart, ScriptContent>>(this)

    override fun prepare(node: JsonTree.Node, context: ArgumentContext) {
        val section = node.validSection() ?: return

        val scripts = analyzeParts(section.value.content).mapNotNull { part ->
            if (part.language != null) {
                val executor = part.language.executor
                val compiledScript = executor.build(part.content, SimpleScriptEnvironment())
                part to compiledScript
            } else null
        }
        scripsCatcher.catch(context) { ConcurrentHashMap(scripts.toMap()) }
    }

    override fun resolve(node: JsonTree.Node, context: ArgumentContext) {
        val section = node.validSection() ?: return
        val resolved = analyzeParts(section.value.content).joinToString("") { part ->
            if (part.language != null) {
                // 获取脚本
                val script = scripsCatcher.catch(context) { ConcurrentHashMap() }.getOrElse(part) {
                    LiteralScriptContent(part.content, part.language.executor)
                }
                // 评估脚本并返回结果
                script.executor.evaluate(script, createEnvironment(context)).toString()
            } else part.content
        }
        section.literal(resolved)
    }

    fun analyzeParts(content: String): List<ScriptPart> {
        return reader.readToFlatten(content).map {
            if (it.isVariable) {
                val index = it.text.indexOf(':')
                // 获取脚本语言
                var language: ScriptType? = null
                if (index != -1) language = ScriptType.match(it.text.substring(0, index))
                // 读取脚本文本
                val content = it.text.substring(index + 1)
                ScriptPart(content, language ?: ScriptManager.defaultLanguage)
            } else ScriptPart(it.text, null)
        }
    }

    @AutoRegister
    object InlineScriptTagResolver : ItemTagResolver {
        override val alias = arrayOf("script")
        override fun resolve(args: List<String>, context: ArgumentContext): String? {
            if (args.isEmpty()) return null
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
            val executor = (language ?: ScriptManager.defaultLanguage).executor

            val part = ScriptPart(content, language ?: ScriptManager.defaultLanguage)

            val script: ScriptContent = scripsCatcher.catch(context) { ConcurrentHashMap() }
                .getOrElse(part) { LiteralScriptContent(content, executor) }

            // 评估脚本并返回结果
            return executor.evaluate(script, createEnvironment(context)).toString()
        }
    }

    @JvmStatic
    private fun createEnvironment(context: ArgumentContext) = context.scriptEnv().apply {
        // 导入变量表
        bindings.putAll(context.varsMap())
    }

    /** 标签读取器 **/
    @JvmStatic
    private val reader = VariableReader("{{", "}}")

}