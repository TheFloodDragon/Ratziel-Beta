package cn.fd.ratziel.module.script.block.provided

import cn.fd.ratziel.core.function.ArgumentContext
import cn.fd.ratziel.core.function.SimpleContext
import cn.fd.ratziel.module.script.ScriptManager
import cn.fd.ratziel.module.script.ScriptType
import cn.fd.ratziel.module.script.api.ScriptContent
import cn.fd.ratziel.module.script.api.ScriptExecutor
import cn.fd.ratziel.module.script.block.BlockParser
import cn.fd.ratziel.module.script.block.ExecutableBlock
import cn.fd.ratziel.module.script.impl.LiteralScriptContent
import cn.fd.ratziel.module.script.internal.NonStrictCompilation
import cn.fd.ratziel.module.script.util.scriptEnv
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.jetbrains.kotlin.utils.addToStdlib.measureTimeMillisWithResult
import taboolib.common.platform.function.debug

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

    /** 编译后的脚本 **/
    lateinit var compiledScript: ScriptContent
        @Synchronized private set
        @Synchronized get

    init {
        if (executor is NonStrictCompilation) {
            compile(SimpleContext()) // 预编译脚本
        }
    }

    /**
     * 编译脚本
     */
    fun compile(context: ArgumentContext): Throwable? {
        // 不重复编译
        if (!::compiledScript.isInitialized) {
            try {
                val environment = context.scriptEnv()
                compiledScript = executor.build(source, environment)
            } catch (e: Exception) {
                return e
            }
        }
        return null
    }

    override fun execute(context: ArgumentContext): Any? {
        measureTimeMillisWithResult {
            // 获取环境
            val environment = context.scriptEnv()
            // 获取脚本
            val script = if (::compiledScript.isInitialized) {
                compiledScript
            } else {
                LiteralScriptContent(source, executor)
            }
            // 评估
            script.executor.evaluate(script, environment)
        }.also { (time, result) ->
            debug("[TIME MARK] ScriptBlock(${::compiledScript.isInitialized}) executed in $time ms. Content: $source")
            return result
        }
    }

    class Parser : BlockParser {

        /** 创建的语句块列表 **/
        val blocks: MutableList<ScriptBlock> = ArrayList()

        /**
         * 每个语言使用一个执行器
         * 该表用来记录每个语言的执行器实例
         */
        private val executors: MutableMap<ScriptType, ScriptExecutor> = HashMap()

        /** 当前执行器 **/
        private var currentExecutor: ScriptExecutor = executors.computeIfAbsent(ScriptManager.defaultLanguage) { it.newExecutor() }

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
                    currentExecutor = executors.computeIfAbsent(type) { it.newExecutor() } // 获取或创建执行器
                    // 使用调度器解析结果
                    val result = scheduler.parse(entry.value, scheduler)
                    // 恢复上一个执行器
                    currentExecutor = lastExecutor
                    // 返回结果
                    return result
                }
            }
            // 解析字符串脚本
            else {
                var content: String? = null
                if (element is JsonPrimitive && element.isString) {
                    content = element.content
                } else if (element is JsonArray && element.all { it is JsonPrimitive }) {
                    content = element.joinToString("\n")
                }
                if (content != null) {
                    val block = ScriptBlock(content, currentExecutor)
                    blocks.add(block) // 记录一下
                    return block
                }
            }
            return null
        }

    }

}