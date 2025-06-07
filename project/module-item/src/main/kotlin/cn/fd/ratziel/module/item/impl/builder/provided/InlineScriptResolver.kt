package cn.fd.ratziel.module.item.impl.builder.provided

import cn.fd.ratziel.core.function.ArgumentContext
import cn.fd.ratziel.module.item.api.builder.ItemSectionResolver
import cn.fd.ratziel.module.item.api.builder.ItemStream
import cn.fd.ratziel.module.item.internal.IdentifiedCache
import cn.fd.ratziel.module.script.ScriptManager
import cn.fd.ratziel.module.script.ScriptType
import cn.fd.ratziel.module.script.api.ScriptContent
import cn.fd.ratziel.module.script.util.scriptEnv
import cn.fd.ratziel.platform.bukkit.util.player
import taboolib.common.util.VariableReader
import java.util.concurrent.ConcurrentHashMap

/**
 * InlineScriptResolver
 *
 * @author TheFloodDragon
 * @since 2025/6/7 19:08
 */
object InlineScriptResolver : ItemSectionResolver {

    private val cache = IdentifiedCache<MutableMap<String, ScriptContent>>()

    override fun resolve(section: String, context: ArgumentContext): String {
        val parts = reader.readToFlatten(section)
        return if (parts.isNotEmpty()) {
            parts.joinToString("") {
                if (it.isVariable) {
                    resolveInlineScript(it.text, context)
                } else it.text
            }
        } else section
    }

    @JvmStatic
    private fun resolveInlineScript(content: String, context: ArgumentContext): String {
        val index = content.indexOf(':')
        // 获取脚本语言
        val language = if (index != -1) {
            ScriptType.match(content.substring(0, index).trim()) ?: ScriptManager.defaultLanguage
        } else ScriptManager.defaultLanguage

        // 创建执行器
        val executor = language.newExecutor()

        val text = content.substring(index + 1)
        val environment = createEnvironment(context)

        // 构建脚本
        val stream = context.popOrNull(ItemStream::class.java)
        val script = if (stream != null) {
            // 缓存机制
            cache.map.computeIfAbsent(stream.identifier) { ConcurrentHashMap() }
                .computeIfAbsent(text) { executor.build(text, environment) }
        } else executor.build(text, environment)

        // 评估脚本并返回结果
        return executor.evaluate(script, environment).toString()
    }

    @JvmStatic
    private fun createEnvironment(context: ArgumentContext) = context.scriptEnv().apply {
        set("player", context.player())
    }

    /** 标签读取器 **/
    @JvmStatic
    private val reader = VariableReader("{{", "}}")

}