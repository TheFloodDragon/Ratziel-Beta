package cn.fd.ratziel.common.block.provided

import cn.fd.ratziel.common.block.BlockContext
import cn.fd.ratziel.common.block.BlockParser
import cn.fd.ratziel.common.block.ExecutableBlock
import cn.fd.ratziel.core.contextual.ArgumentContext
import cn.fd.ratziel.core.contextual.AttachedContext
import cn.fd.ratziel.core.util.resolveBy
import cn.fd.ratziel.module.script.ScriptManager
import cn.fd.ratziel.module.script.ScriptType
import cn.fd.ratziel.module.script.api.*
import cn.fd.ratziel.module.script.element.ScriptElementHandler
import cn.fd.ratziel.module.script.element.ScriptFile
import cn.fd.ratziel.module.script.impl.CompilationPreference
import cn.fd.ratziel.module.script.imports.GroupImports
import cn.fd.ratziel.module.script.util.scriptEnv
import kotlinx.serialization.json.*
import org.jetbrains.kotlin.utils.addToStdlib.measureTimeMillisWithResult
import taboolib.common.platform.function.debug
import taboolib.common.reflect.hasAnnotation
import taboolib.common5.cbool
import kotlin.concurrent.getOrSet

/**
 * ScriptBlock
 *
 * @author TheFloodDragon
 * @since 2024/10/2 18:31
 */
open class ScriptBlock(
    /** 脚本原始内容 */
    val source: String,
    /** 脚本执行器 */
    val executor: ScriptExecutor,
    /** 是否需要编译脚本 **/
    needCompile: Boolean = true,
    /** 导入组 **/
    val imports: GroupImports? = null,
) : ExecutableBlock {

    constructor(source: String, executor: ScriptExecutor, context: BlockContext) : this(
        source, executor, context["caching"]?.cbool ?: true, GroupImports.catcher[context.attached]
    )

    /**
     * 是否编译脚本
     */
    open val compile = needCompile && executor::class.java.hasAnnotation(CompilationPreference::class.java)

    /** 编译后的脚本 **/
    open val script: ScriptContent = compileOrLiteral(executor, source)

    override fun execute(context: ArgumentContext): Any? {
        // 执行脚本
        measureTimeMillisWithResult {
            // 获取当前线程的脚本环境 (没有就创建一个)
            val threadEnv = threadLocal.getOrSet { context to context.scriptEnv().copy() }
            // 上下文一致, 那就直接用
            val environment = if (threadEnv.first === context) threadEnv.second else {
                // 若上下文改变则重新创建脚本环境
                context.scriptEnv().copy().also { copied ->
                    threadLocal.remove()
                    threadLocal.set(context to copied)
                }
            }
            // 评估脚本
            executor.evaluate(script, environment)
        }.also { (time, result) ->
            debug("[TIME MARK] ScriptBlock(${script !is LiteralScriptContent}) executed in $time ms. Content: $source")
            return result
        }
    }

    /**
     * 尝试编译脚本, 若编译失败或者语言不支持空脚本环境, 则返回一个脚本文本内容
     */
    open fun compileOrLiteral(executor: ScriptExecutor, script: String): ScriptContent {
        if (this.compile) {
            // 预编译脚本
            try {
                // 带环境的编译脚本
                return executor.build(LiteralScriptSource(script), createEnvironment())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return LiteralScriptContent(script, executor)
    }

    open fun createEnvironment() = ScriptEnvironment().apply {
        // 处理导入组
        if (imports != null) GroupImports.catcher(context) { it.combine(imports) }
    }

    override fun toString() = "ScriptBlock(executor=$executor, source=$source)"

    /**
     * ScriptFileBlock
     */
    class ScriptFileBlock(val scriptFile: ScriptFile, context: BlockContext) : ScriptBlock(scriptFile.source, scriptFile.executor, context) {
        /**
         * 使用编译后的脚本
         */
        override val script = scriptFile.compile(createEnvironment())
    }

    companion object {

        /**
         * 用于在多线程环境下克隆脚本环境, 避免不同线程间的脚本环境使用冲突.
         */
        @JvmStatic
        private val threadLocal = ThreadLocal<Pair<ArgumentContext, ScriptEnvironment>>()

        /**
         * 显示脚本解析选项
         */
        const val EXPLICIT_SCRIPT_OPTION = "explicit-script"

    }

    object Parser : BlockParser {

        /** 当前执行器 **/
        private val currentExecutor = AttachedContext.catcher(this) { ScriptManager.defaultLanguage.executor }

        override fun parse(element: JsonElement, context: BlockContext): ExecutableBlock? {
            if (element is JsonObject) {
                // 寻找脚本导入并处理
                val importsSection = element["imports"] ?: element["import"]
                if (importsSection != null && importsSection is JsonArray) {
                    val lines = importsSection.mapNotNull { (it as? JsonPrimitive)?.contentOrNull }
                    val imports = GroupImports.parse(lines, context.workFile?.parentFile)
                    // 添加进上下文中
                    GroupImports.catcher(context.attached) { it.combine(imports) }
                }

                // 寻找切换脚本语言的
                val entry = element.entries.find { it.key.startsWith('\$') }
                // 检查开头 (是否为转换语言的)
                if (entry != null) {
                    val type = ScriptType.matchOrThrow(entry.key.drop(1))
                    // 记录当前执行器
                    val lastExecutor = currentExecutor[context.attached]
                    // 设置当前执行器
                    currentExecutor[context.attached] = type.executor // 获取或创建执行器
                    val explicit = context[EXPLICIT_SCRIPT_OPTION] ?: true
                    context[EXPLICIT_SCRIPT_OPTION] = false // 切换语言的时候可以直接解析字符串为脚本
                    // 使用调度器解析结果
                    val result = context.parse(entry.value)
                    // 恢复上一个执行器
                    currentExecutor[context.attached] = lastExecutor
                    context[EXPLICIT_SCRIPT_OPTION] = explicit
                    // 返回结果
                    return result
                }

                // 寻找脚本文件执行
                val fileSection = (element["script"] as? JsonPrimitive)?.contentOrNull
                if (fileSection != null) {
                    val file = context.workFile?.parentFile.resolveBy(fileSection)
                    // 查找脚本文件
                    val scriptFile = ScriptElementHandler.scriptFiles[file]
                    if (scriptFile != null) {
                        // 返回含脚本文件的脚本块
                        return ScriptFileBlock(scriptFile, context)
                    } else error("No defined script file $file!")
                }

            }

            // 默认情况下开启显式脚本, 不解析字符串类型
            if (context[EXPLICIT_SCRIPT_OPTION]?.cbool ?: true) return null

            // 解析字符串脚本
            var content: String? = null
            if (element is JsonPrimitive && element.isString) {
                content = element.content
            } else if (element is JsonArray && element.all { it is JsonPrimitive }) {
                content = element.joinToString("\n")
            }
            return if (content != null) ScriptBlock(content, currentExecutor[context.attached], context) else null
        }

    }

}