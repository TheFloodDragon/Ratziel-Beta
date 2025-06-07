package cn.fd.ratziel.module.item.impl.builder.provided

import cn.fd.ratziel.core.function.ArgumentContext
import cn.fd.ratziel.core.serialization.json.JsonTree
import cn.fd.ratziel.module.item.api.builder.ItemSectionResolver
import cn.fd.ratziel.module.script.ScriptManager
import cn.fd.ratziel.module.script.ScriptType
import cn.fd.ratziel.module.script.api.ScriptContent
import cn.fd.ratziel.module.script.util.scriptEnv
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import taboolib.common.util.VariableReader
import java.util.concurrent.ConcurrentHashMap

/**
 * InlineScriptResolver
 *
 * @author TheFloodDragon
 * @since 2025/6/7 19:08
 */
object InlineScriptResolver : ItemSectionResolver {

    private val cache: MutableMap<String, ScriptContent> = ConcurrentHashMap()

    override fun resolve(node: JsonTree.Node, context: ArgumentContext) {
        // 单个节点只解析 Primitive 类型, 即字符串
        if (node !is JsonTree.PrimitiveNode) return
        // 节点内容
        val value = node.value
        // 判断有效节点
        if (value.isString && value !is JsonNull) {
            // 解析字符串
            val resolved = resolveString(node.value.content, context)
            // 更新节点内容
            node.value = JsonPrimitive(resolved)
        }
    }

    @JvmStatic
    private fun resolveString(content: String, context: ArgumentContext): String {
        val parts = reader.readToFlatten(content)
        return if (parts.isNotEmpty()) {
            parts.joinToString("") {
                if (it.isVariable) {
                    resolveInlineScript(it.text, context)
                } else it.text
            }
        } else content
    }

    @JvmStatic
    private fun resolveInlineScript(str: String, context: ArgumentContext): String {
        val index = str.indexOf(':')
        // 获取脚本语言
        val language = if (index != -1) {
            ScriptType.match(str.substring(0, index)) ?: ScriptManager.defaultLanguage
        } else ScriptManager.defaultLanguage
        // 创建执行器
        val executor = language.newExecutor()
        // 执行脚本
        val content = str.substring(index + 1)
        val environment = context.scriptEnv()
        val script = cache.computeIfAbsent(content) {
            executor.build(it, environment)
        }
        return executor.evaluate(script, environment).toString()
    }

    /** 标签读取器 **/
    @JvmStatic
    private val reader = VariableReader("{{", "}}")

}