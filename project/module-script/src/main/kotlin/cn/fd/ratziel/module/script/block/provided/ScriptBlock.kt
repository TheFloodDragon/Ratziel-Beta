package cn.fd.ratziel.module.script.block.provided

import cn.fd.ratziel.core.contextual.ArgumentContext
import cn.fd.ratziel.core.contextual.AttachedContext
import cn.fd.ratziel.core.functional.Replenishment
import cn.fd.ratziel.module.script.ScriptManager
import cn.fd.ratziel.module.script.ScriptType
import cn.fd.ratziel.module.script.api.Importable
import cn.fd.ratziel.module.script.api.LiteralScriptContent
import cn.fd.ratziel.module.script.api.ScriptContent
import cn.fd.ratziel.module.script.api.ScriptExecutor
import cn.fd.ratziel.module.script.block.BlockContext
import cn.fd.ratziel.module.script.block.BlockParser
import cn.fd.ratziel.module.script.block.ExecutableBlock
import cn.fd.ratziel.module.script.impl.ScriptEnvironmentImpl
import cn.fd.ratziel.module.script.internal.NonStrictCompilation
import cn.fd.ratziel.module.script.util.scriptEnv
import kotlinx.serialization.json.JsonArray
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
    override val context: BlockContext = BlockContext.withoutScheduler(),
) : ExecutableBlock.ContextualBlock {

    /** 编译后的脚本 **/
    val script: ScriptContent = compileOrLiteral(executor, source)

    /**
     * 脚本引擎补充器 (提高并行执行多编译脚本的性能)
     */
    private val engineReplenishing: Replenishment<CompletableFuture<ScriptEnvironmentImpl>>? =
        if (executor is Importable) {
            Replenishment {
                CompletableFuture.supplyAsync {
                    val environment = ScriptEnvironmentImpl()
                    // 导入导入件
                    executor.importTo(environment, importsCatcher[context.attached])
                    environment
                }
            }
        } else null

    override fun execute(context: ArgumentContext): Any? {
        // 环境处理
        val contextEnvironment = context.scriptEnv()
        val environment = engineReplenishing?.getValue()?.get()?.apply {
            bindings = contextEnvironment.bindings // 导入环境的绑定键
        } ?: contextEnvironment // 不支持补充就直接用环境的
        // 执行脚本
        measureTimeMillisWithResult {
            executor.evaluate(script, environment)
        }.also { (time, result) ->
            debug("[TIME MARK] ScriptBlock(${script::class.java != LiteralScriptContent::class.java}) executed in $time ms. Content: $source")
            return result
        }
    }

    /**
     * 尝试编译脚本, 若编译失败或者语言不支持空脚本环境, 则返回一个脚本文本内容
     */
    fun compileOrLiteral(executor: ScriptExecutor, script: String): ScriptContent {
        if (executor is NonStrictCompilation) {
            // 预编译脚本
            try {
                return executor.build(script)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return LiteralScriptContent(script, executor)
    }

    override fun toString() = "ScriptBlock(executor=$executor, source=$source)"

    companion object {

        /**
         * 导入组获取器 (默认使用全局导入组)
         */
        internal val importsCatcher = AttachedContext.catcher(this) { ScriptManager.globalGroup }

    }

    class Parser : BlockParser {

        /** 当前执行器 **/
        private var currentExecutor: ScriptExecutor = ScriptManager.defaultLanguage.executor

        override fun parse(element: JsonElement, context: BlockContext): ExecutableBlock? {
            if (element is JsonObject) {
                val entry = element.entries.first()
                val key = entry.key.trim()
                // 检查开头 (是否为转换语言的)
                if (key.startsWith('\$')) {
                    val type = ScriptType.matchOrThrow(key.drop(1))
                    // 记录当前执行器
                    val lastExecutor = currentExecutor
                    // 设置当前执行器
                    currentExecutor = type.executor // 获取或创建执行器
                    // 使用调度器解析结果
                    val result = context.parse(entry.value)
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
                    return ScriptBlock(content, currentExecutor, context)
                }
            }
            return null
        }

    }

}