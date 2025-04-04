package cn.fd.ratziel.module.script.block.provided

import cn.fd.ratziel.core.function.ArgumentContext
import cn.fd.ratziel.module.script.ScriptManager
import cn.fd.ratziel.module.script.ScriptType
import cn.fd.ratziel.module.script.api.ScriptContent
import cn.fd.ratziel.module.script.api.ScriptExecutor
import cn.fd.ratziel.module.script.block.BlockParser
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
data class ScriptBlock(val script: ScriptContent) : ExecutableBlock {

    constructor(script: String, executor: ScriptExecutor) : this(executor.build(script))

    override fun execute(context: ArgumentContext): Any? {
        val environment = context.scriptEnv() ?: SimpleScriptEnv()
        return script.executor.evaluate(script, environment)
    }

    companion object Parser : BlockParser {

        private const val MARK_TOGGLE = '\$'

        override fun parse(element: JsonElement, parser: BlockParser): ExecutableBlock? {
            return if (element is JsonObject) {
                val pair = find(element) ?: return null
                this.parseBasic(pair.first, pair.second, parser)
            } else {
                this.parseBasic(element, ScriptManager.defaultLanguage.executorOrThrow, parser)
            }
        }

        private fun parseBasic(element: JsonElement, executor: ScriptExecutor, parent: BlockParser): ExecutableBlock? {
            if (element is JsonArray) {
                return MultiLineBlock(element.map { parent.parse(it) ?: return null })
            } else if (element is JsonPrimitive) {
                return ScriptBlock(element.content, executor)
            }
            return null
        }

        private fun find(element: JsonObject): Pair<JsonElement, ScriptExecutor>? {
            element.firstNotNullOfOrNull {
                val key = it.key.trim()
                // 检查开头 (是否为转换语言的)
                if (key.startsWith(MARK_TOGGLE)) {
                    val type = ScriptType.matchOrThrow(key.drop(1))
                    return it.value to type.executorOrThrow
                }
            }
            return null
        }

    }

}