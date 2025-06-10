package cn.fd.ratziel.module.item.impl.builder.provided

import cn.fd.ratziel.common.element.registry.AutoRegister
import cn.fd.ratziel.core.function.ArgumentContext
import cn.fd.ratziel.module.item.api.builder.ItemSectionResolver
import cn.fd.ratziel.module.item.api.builder.ItemStream
import cn.fd.ratziel.module.item.api.builder.ItemTagResolver
import cn.fd.ratziel.module.item.internal.IdentifiedCache
import cn.fd.ratziel.module.script.ScriptManager
import cn.fd.ratziel.module.script.ScriptType
import cn.fd.ratziel.module.script.api.ScriptContent
import cn.fd.ratziel.module.script.impl.LiteralScriptContent
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
@AutoRegister
object InlineScriptResolver : ItemSectionResolver, ItemTagResolver {

    override val alias = arrayOf("script")

    private val cache = IdentifiedCache<MutableMap<String, ScriptContent>>()

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
        return resolveInlineScript(language, content, context)
    }

    override fun resolve(section: String, context: ArgumentContext): String {
        val parts = reader.readToFlatten(section)
        return if (parts.isNotEmpty()) {
            parts.joinToString("") {
                if (it.isVariable) {
                    val index = it.text.indexOf(':')
                    // 获取脚本语言
                    var language: ScriptType? = null
                    if (index != -1) language = ScriptType.match(it.text.substring(0, index))
                    // 读取脚本文本
                    val script = it.text.substring(index + 1)
                    // 解析
                    resolveInlineScript(language, script, context)
                } else it.text
            }
        } else section
    }

    @JvmStatic
    private fun resolveInlineScript(language: ScriptType?, content: String, context: ArgumentContext): String {
        // 创建执行器
        val executor = (language ?: ScriptManager.defaultLanguage).newExecutor()
        val environment = createEnvironment(context)

        // 构建脚本
        val stream = context.popOrNull(ItemStream::class.java)
        val script = if (stream != null) {
            // 缓存机制
            cache.map.computeIfAbsent(stream.identifier) { ConcurrentHashMap() }
                .computeIfAbsent(content) { executor.build(content, environment) }
        } else LiteralScriptContent(content, executor)

        // 评估脚本并返回结果
        return executor.evaluate(script, environment).toString()
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