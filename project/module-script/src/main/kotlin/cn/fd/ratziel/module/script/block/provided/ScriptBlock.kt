package cn.fd.ratziel.module.script.block.provided

import cn.fd.ratziel.core.contextual.ArgumentContext
import cn.fd.ratziel.core.util.resolveOrAbsolute
import cn.fd.ratziel.module.script.ScriptManager
import cn.fd.ratziel.module.script.ScriptType
import cn.fd.ratziel.module.script.api.LiteralScriptContent
import cn.fd.ratziel.module.script.api.ScriptContent
import cn.fd.ratziel.module.script.api.ScriptExecutor
import cn.fd.ratziel.module.script.block.BlockContext
import cn.fd.ratziel.module.script.block.BlockParser
import cn.fd.ratziel.module.script.block.ExecutableBlock
import cn.fd.ratziel.module.script.element.ScriptElementHandler
import cn.fd.ratziel.module.script.element.ScriptFile
import cn.fd.ratziel.module.script.impl.ScriptEnvironmentImpl
import cn.fd.ratziel.module.script.imports.GroupImports
import cn.fd.ratziel.module.script.internal.NonStrictCompilation
import cn.fd.ratziel.module.script.util.scriptEnv
import kotlinx.serialization.json.*
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
    /** 导入组 **/
    val imports: GroupImports? = null,
    /** 脚本文件 **/
    val scriptFile: ScriptFile? = null,
) : ExecutableBlock {

    constructor(source: String, executor: ScriptExecutor, context: BlockContext) : this(source, executor, GroupImports.catcher[context.attached])

    constructor(scriptFile: ScriptFile, imports: GroupImports) : this(scriptFile.source, scriptFile.executor, imports, scriptFile)

    constructor(scriptFile: ScriptFile, context: BlockContext) : this(scriptFile, GroupImports.catcher[context.attached])

    /** 编译后的脚本 **/
    val script: ScriptContent = scriptFile?.compiled ?: compileOrLiteral(executor, source)

    override fun execute(context: ArgumentContext): Any? {
        // 执行脚本
        measureTimeMillisWithResult {
            executor.evaluate(script, context.scriptEnv())
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
                // 处理导入组
                val environment = ScriptEnvironmentImpl()
                if (imports != null) GroupImports.catcher[environment.context] = imports
                // 带环境的编译脚本
                return executor.build(script, environment)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return LiteralScriptContent(script, executor)
    }

    override fun toString() = "ScriptBlock(executor=$executor, source=$source)"

    class Parser : BlockParser {

        /** 当前执行器 **/
        private var currentExecutor: ScriptExecutor = ScriptManager.defaultLanguage.executor

        override fun parse(element: JsonElement, context: BlockContext): ExecutableBlock? {
            if (element is JsonObject) {
                // 寻找脚本导入并处理
                val importsSection = element["imports"] ?: element["import"]
                if (importsSection != null && importsSection is JsonArray) {
                    val lines = importsSection.mapNotNull { (it as? JsonPrimitive)?.contentOrNull }
                    val imports = GroupImports.parse(lines, context.workFile)
                    // 添加进上下文中
                    val originalImports = GroupImports.catcher[context.attached]
                    GroupImports.catcher[context.attached] = originalImports.combine(imports)
                }

                // 寻找切换脚本语言的
                val entry = element.entries.find { it.key.startsWith('\$') }
                // 检查开头 (是否为转换语言的)
                if (entry != null) {
                    val type = ScriptType.matchOrThrow(entry.key.drop(1))
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

                // 寻找脚本文件执行
                val fileSection = (element["script"] as? JsonPrimitive)?.contentOrNull
                if (fileSection != null) {
                    val file = context.workFile?.parentFile.resolveOrAbsolute(fileSection)
                    // 查找脚本文件
                    val scriptFile = ScriptElementHandler.scriptFiles[file]
                    if (scriptFile != null) {
                        // 返回含脚本文件的脚本块
                        return ScriptBlock(scriptFile, context)
                    } else error("No defined script file $file!")
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