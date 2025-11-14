package cn.fd.ratziel.common.block.provided

import cn.fd.ratziel.common.block.*
import cn.fd.ratziel.common.block.conf.explicitScriptParsing
import cn.fd.ratziel.common.block.conf.scriptCaching
import cn.fd.ratziel.common.block.conf.scriptImporting
import cn.fd.ratziel.common.block.conf.scriptName
import cn.fd.ratziel.core.contextual.ArgumentContext
import cn.fd.ratziel.core.util.resolveBy
import cn.fd.ratziel.module.script.ScriptManager
import cn.fd.ratziel.module.script.api.*
import cn.fd.ratziel.module.script.element.ScriptElementHandler
import cn.fd.ratziel.module.script.element.ScriptFile
import cn.fd.ratziel.module.script.importing.GroupImports
import cn.fd.ratziel.module.script.util.eval
import cn.fd.ratziel.module.script.util.scriptEnv
import kotlinx.serialization.json.*
import org.jetbrains.kotlin.utils.addToStdlib.measureTimeMillisWithResult
import taboolib.common.platform.function.debug
import taboolib.common.platform.function.warning

/**
 * ScriptBlock
 *
 * @author TheFloodDragon
 * @since 2024/10/2 18:31
 */
open class ScriptBlock(
    /** 脚本原始内容 */
    val source: String,
    /** 脚本类型 */
    val language: ScriptType,
    /** 脚本名称 **/
    val scriptName: String? = null,
    /** 是否需要编译脚本 **/
    needCompile: Boolean = true,
    /** 导入组 **/
    val imports: GroupImports? = null,
) : ExecutableBlock {

    constructor(source: String, language: ScriptType, context: BlockContext) : this(
        source, language, context[BlockConfigurationKeys.scriptName],
        context[BlockConfigurationKeys.scriptCaching],
        context[BlockConfigurationKeys.scriptImporting]
    )

    /**
     * 是否编译脚本
     */
    open val compile = needCompile && language.preference.suggestingCompilation

    /** 编译后的脚本 **/
    open val script: ScriptContent = compileOrLiteral(language, source)

    override fun execute(context: ArgumentContext): Any? {
        // 执行脚本
        measureTimeMillisWithResult {
            runCatching {
                // 评估脚本
                script.eval(context.scriptEnv())
            }.onFailure { warning("Failed to execute script:", it.stackTraceToString()) }
        }.also { (time, result) ->
            debug("[TIME MARK] ScriptBlock(${script !is LiteralScriptContent}) executed in $time ms. Content: $source")
            return result.getOrNull()
        }
    }

    /**
     * 尝试编译脚本, 若编译失败或者语言不支持空脚本环境, 则返回一个脚本文本内容
     */
    open fun compileOrLiteral(language: ScriptType, script: String): ScriptContent {
        if (this.compile) {
            // 预编译脚本
            val compiled = runCatching {
                language.executor.build(ScriptSource.literal(script, language), createEnvironment())
            }.onFailure { warning("Failed to compile script:", it.stackTraceToString()) }.getOrNull()
            if (compiled != null) return compiled
        }
        return ScriptContent.literal(script, language)
    }

    open fun createEnvironment() = ScriptEnvironment().apply {
        // 处理导入组
        if (imports != null) GroupImports.catcher(context) { it.combine(imports) }
    }

    override fun toString() = "ScriptBlock(language=$language, source=$source)"

    /**
     * ScriptFileBlock
     */
    class ScriptFileBlock(val scriptFile: ScriptFile, context: BlockContext) : ScriptBlock(scriptFile.source, scriptFile.language, context) {
        /**
         * 使用编译后的脚本
         */
        override val script = scriptFile.compile(createEnvironment())
    }

    object Parser : BlockParser {

        /** 当前执行器 **/
        private val currentLanguage by BlockConfigurationKeys.weakKey { ScriptManager.defaultLanguage }

        override fun parse(element: JsonElement, context: BlockContext): ExecutableBlock? = with(context) {
            if (element is JsonObject) {
                // 寻找脚本导入并处理
                val importsSection = element["imports"] ?: element["import"]
                if (importsSection != null && importsSection is JsonArray) {
                    val lines = importsSection.mapNotNull { (it as? JsonPrimitive)?.contentOrNull }
                    val imports = GroupImports.parse(lines, workFile()?.parentFile)
                    // 添加进上下文中
                    scriptImporting.put(scriptImporting().combine(imports))
                }

                // 寻找切换脚本语言的
                val entry = element.entries.find { it.key.startsWith('\$') }
                // 检查开头 (是否为转换语言的)
                if (entry != null) {
                    val type = ScriptType.matchOrThrow(entry.key.drop(1))
                    // 记录当前语言类型
                    val lastLanguage = currentLanguage()
                    // 设置当前语言类型
                    currentLanguage(type)
                    val explicit = explicitScriptParsing()
                    explicitScriptParsing(false) // 切换语言的时候可以直接解析字符串为脚本
                    // 使用调度器解析结果
                    val result = context.parse(entry.value)
                    // 恢复上一个执行器
                    currentLanguage(lastLanguage)
                    explicitScriptParsing(explicit)
                    // 返回结果
                    return result
                }

                // 寻找脚本文件执行
                val fileSection = (element["script"] as? JsonPrimitive)?.contentOrNull
                if (fileSection != null) {
                    val file = workFile()?.parentFile.resolveBy(fileSection)
                    // 查找脚本文件
                    val scriptFile = ScriptElementHandler.scriptFiles[file]
                    if (scriptFile != null) {
                        // 返回含脚本文件的脚本块
                        return ScriptFileBlock(scriptFile, context)
                    } else error("No defined script file $file!")
                }

            }

            // 默认情况下开启显式脚本, 不解析字符串类型
            if (explicitScriptParsing()) return null

            // 解析字符串脚本
            var content: String? = null
            if (element is JsonPrimitive && element.isString) {
                content = element.content
            } else if (element is JsonArray && element.all { it is JsonPrimitive }) {
                content = element.joinToString("\n") { (it as JsonPrimitive).content }
            }
            return if (content != null) ScriptBlock(content, currentLanguage(), context) else null
        }

    }

}