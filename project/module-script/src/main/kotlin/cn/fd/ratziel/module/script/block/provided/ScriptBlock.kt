package cn.fd.ratziel.module.script.block.provided

import cn.fd.ratziel.core.function.ArgumentContext
import cn.fd.ratziel.module.script.ScriptManager
import cn.fd.ratziel.module.script.ScriptType
import cn.fd.ratziel.module.script.api.ScriptContent
import cn.fd.ratziel.module.script.api.ScriptExecutor
import cn.fd.ratziel.module.script.block.ExecutableBlock
import cn.fd.ratziel.module.script.impl.SimpleScriptEnv
import cn.fd.ratziel.module.script.util.scriptEnv
import kotlinx.serialization.json.*
import org.jetbrains.kotlin.utils.addToStdlib.measureTimeMillisWithResult
import taboolib.common.platform.function.debug
import java.util.concurrent.CompletableFuture

/**
 * ScriptBlock TODO Refactor
 *
 * @author TheFloodDragon
 * @since 2024/10/2 18:31
 */
class ScriptBlock(val scriptSource: String, val executor: ScriptExecutor) : ExecutableBlock {

    lateinit var script: ScriptContent
        @Synchronized private set
        @Synchronized get

    init {
        // 尝试预编译
        CompletableFuture.supplyAsync {
            try {
                val compiled = executor.build(scriptSource, SimpleScriptEnv())
                if (!::script.isInitialized) {
                    script = compiled
                }
            } catch (_: Exception) {
            }
        }
    }

    override fun execute(context: ArgumentContext): Any? {
        measureTimeMillisWithResult {
            val environment = context.scriptEnv() ?: SimpleScriptEnv()
            // 初次运行编译
            if (!::script.isInitialized) {
                script = executor.build(scriptSource, environment)
            }
            // 评估
            script.executor.evaluate(script, environment)
        }.let { (time, result) ->
            debug("[TIME MARK] ScriptBlock executed in $time ms. Content: $scriptSource")
            return result
        }
    }

    class Parser {

        var currentExecutor = ScriptManager.defaultLanguage.executorOrThrow

        fun parse(element: JsonElement): ExecutableBlock? {
            return parse0(element) ?: if (element is JsonPrimitive) ValueBlock(element.contentOrNull) else null
        }

        fun parse0(element: JsonElement): ExecutableBlock? {
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