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
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.jetbrains.kotlin.utils.addToStdlib.measureTimeMillisWithResult
import taboolib.common.platform.function.debug
import java.util.concurrent.CompletableFuture

/**
 * ScriptBlock
 *
 * @author TheFloodDragon
 * @since 2024/10/2 18:31
 */
class ScriptBlock(
    /** 脚本原始内容 */
    val source: String,
    /** 脚本执行器 */
    val executor: ScriptExecutor,
) : ExecutableBlock {

    /** 脚本 */
    lateinit var script: ScriptContent
        @Synchronized private set
        @Synchronized get

    init {
        // 尝试预编译
        CompletableFuture.supplyAsync {
            try {
                val compiled = executor.build(source, SimpleScriptEnv())
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
                script = executor.build(source, environment)
            }
            // 评估
            script.executor.evaluate(script, environment)
        }.let { (time, result) ->
            debug("[TIME MARK] ScriptBlock executed in $time ms. Content: $source")
            return result
        }
    }

    class Parser : BlockParser {

        var currentExecutor = ScriptManager.defaultLanguage.executorOrThrow

        override fun parse(element: JsonElement, scheduler: BlockParser): ExecutableBlock? {
            if (element is JsonObject && element.size == 1) {
                val entry = element.entries.first()
                val key = entry.key.trim()
                // 检查开头 (是否为转换语言的)
                if (key.startsWith('\$')) {
                    val type = ScriptType.matchOrThrow(key.drop(1))
                    // 记录当前执行器
                    val lastExecutor = currentExecutor
                    // 设置当前执行器
                    currentExecutor = type.executorOrThrow
                    // 使用调度器解析结果
                    val result = scheduler.parse(entry.value, scheduler)
                    // 恢复上一个执行器
                    currentExecutor = lastExecutor
                    // 返回结果
                    return result
                }
            }
            // 解析字符串脚本
            if (element is JsonPrimitive && element.isString) {
                return ScriptBlock(element.content, currentExecutor)
            }
            return null
        }

    }

}