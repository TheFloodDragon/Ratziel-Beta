package cn.fd.ratziel.module.script.block.provided

import cn.fd.ratziel.core.function.ArgumentContext
import cn.fd.ratziel.module.script.ScriptManager
import cn.fd.ratziel.module.script.ScriptType
import cn.fd.ratziel.module.script.api.ScriptContent
import cn.fd.ratziel.module.script.api.ScriptExecutor
import cn.fd.ratziel.module.script.block.ExecutableBlock
import cn.fd.ratziel.module.script.impl.SimpleScriptEnv
import cn.fd.ratziel.module.script.util.scriptEnv
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

/**
 * ScriptBlock
 *
 * @author TheFloodDragon
 * @since 2024/10/2 18:31
 */
class ScriptBlock(val script: ScriptContent) : ExecutableBlock {

    constructor(script: String, executor: ScriptExecutor) : this(executor.build(script))

    override fun execute(context: ArgumentContext): Any? {
        val environment = context.scriptEnv() ?: SimpleScriptEnv()
        return script.executor.evaluate(script, environment)
    }

    class Parser {

        var currentExecutor = ScriptManager.defaultLanguage.executorOrThrow

        fun parse(element: JsonElement): ExecutableBlock? {
            if (element is JsonObject && element.size == 1) {
                val entry = element.entries.first()
                val key = entry.key.trim()
                // 检查开头 (是否为转换语言的)
                if (key.startsWith('\$')) {
                    val type = ScriptType.matchOrThrow(key.drop(1))
                    currentExecutor = type.executorOrThrow
                    return this.parseBasic(entry.value)
                }
            }

            return this.parseBasic(element)
        }

        private fun parseBasic(element: JsonElement): ExecutableBlock? {
            if (element is JsonArray) {
                return ScriptBlock(
                    element.map { (it as? JsonPrimitive)?.content ?: return null }
                        .joinToString("\n"), currentExecutor
                )
            } else if (element is JsonPrimitive) {
                return ScriptBlock(element.content, currentExecutor)
            }
            return null
        }

    }

}