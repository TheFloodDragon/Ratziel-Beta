package cn.fd.ratziel.module.script.lang.js

import cn.fd.ratziel.module.script.api.IntegratedScriptExecutor
import cn.fd.ratziel.module.script.api.ScriptContent
import cn.fd.ratziel.module.script.api.ScriptEnvironment
import cn.fd.ratziel.module.script.api.ScriptSource
import cn.fd.ratziel.module.script.conf.ScriptConfigurationKeys
import cn.fd.ratziel.module.script.conf.scriptImporting
import cn.fd.ratziel.module.script.impl.ReplenishingScript
import cn.fd.ratziel.module.script.importing.ScriptImport
import org.graalvm.polyglot.Context
import org.graalvm.polyglot.Engine
import org.graalvm.polyglot.Source
import javax.script.ScriptEngine


/**
 * GraalJsScriptExecutor
 *
 * 其JSR223的实现完全限制了 GraalJs 的发挥, 所以自此不再在此使用 [ScriptEngine]
 *
 * @author TheFloodDragon
 * @since 2025/4/26 09:56
 */
class GraalJsScriptExecutor : IntegratedScriptExecutor() {

    /**
     * 依照 GraalVM Polyglot 的文档, 代码缓存的数据作为 [Engine] 实例的一部分.
     * 使用同一的 [Engine] 进行代码缓存或许可以提升效率.
     */
    val sharedEngine: Engine = Engine.newBuilder(LANGUAGE_ID)
        .allowExperimentalOptions(true)
        .option("js.ecmascript-version", "latest")
        .option("js.nashorn-compat", "true") // Nashorn 兼容模式
        .build()

    /**
     * 创建一个新的默认的 [Context.Builder]
     */
    fun newContext(): Context = Context.newBuilder(LANGUAGE_ID)
        .hostClassLoader(this::class.java.classLoader)
        .allowAllAccess(true) // 全开了算了
        .engine(sharedEngine) // 绑定分享的 Engine
        .build()

    override fun compile(source: ScriptSource, environment: ScriptEnvironment) =
        CompiledGraalJsScript(
            createSource(source),
            environment, source,
        )

    override fun evaluate(script: ScriptContent, environment: ScriptEnvironment): Any? {
        // 创建运行上下文
        val runtime = createRuntime(environment)
        // 评估脚本源 (不缓存)
        return runtime.eval(createSource(script.source, false))
            .`as`(Any::class.java)
    }

    /**
     * 创建运行时所需的上下文
     */
    fun createRuntime(environment: ScriptEnvironment): Context {
        // 初始化预热上下文
        val context = newContext().importBindings(environment)

        // 导入环境的导入组 (类、包、脚本)
        val imports = environment.configuration[ScriptConfigurationKeys.scriptImporting]

        // 导入类、包
        if (imports.classes.isNotEmpty() || imports.packages.isNotEmpty()) {
            // 导入脚本: 用于在运行时获取类、包
            context.eval(importingScript)
            // 设置成员: 供导入脚本在运行时获取类、包 (外部传入 GroupImports)
            val contextBindings = context.getBindings(LANGUAGE_ID)
            contextBindings.putMember(IMPORTS_MEMBER, imports)
        }

        // 导入脚本
        val scriptImports = imports.getSource<ScriptImport>(JavaScriptLang)
        if (scriptImports.isNotEmpty()) {
            for (import in scriptImports) {
                // 调用脚本执行器的评估函数
                super.eval(import.compiled ?: continue, environment)
            }
        }

        return context
    }

    override fun compiler() = GraalJsScriptExecutor()
    override fun evaluator() = GraalJsScriptExecutor()

    inner class CompiledGraalJsScript(
        script: Source,
        compilationEnv: ScriptEnvironment,
        source: ScriptSource,
    ) : ReplenishingScript<Source, Context>(script, compilationEnv, source, this) {
        override fun preheat() = createRuntime(compilationEnv)
        override fun eval(engine: Context): Any? = engine.eval(script).`as`(Any::class.java)
        override fun initRuntime(engine: Context, runtimeEnv: ScriptEnvironment) {
            // 导入运行时的环境绑定键
            engine.importBindings(runtimeEnv)
        }
    }

    companion object {

        private const val LANGUAGE_ID = "js"
        private const val IMPORTS_MEMBER = "_imports_"

        /**
         * 默认脚本实例
         */
        @JvmField
        val DEFAULT = GraalJsScriptExecutor()

        /**
         * 导入环境的绑定键
         */
        @JvmStatic
        fun Context.importBindings(environment: ScriptEnvironment): Context = this.also { context ->
            // 导入环境的绑定键
            val environmentBindings = environment.bindings
            val contextBindings = context.getBindings(LANGUAGE_ID)
            if (environmentBindings.isNotEmpty()) {
                for ((key, value) in environmentBindings) {
                    contextBindings.putMember(key, value)
                }
            }
        }

        /** 用来导入包和类的脚本 **/
        @JvmStatic
        private val importingScript by lazy {
            internalSource(this::class.java.classLoader.getResourceAsStream("internal/graaljs.importer.js")!!.reader().readText())
        }

        @JvmStatic
        private fun createSource(source: ScriptSource, cached: Boolean = true): Source {
            return Source.newBuilder(LANGUAGE_ID, source.content, source.name ?: "<eval>").cached(cached).build()
        }

        /** 编译内部脚本源 **/
        @JvmStatic
        private fun internalSource(script: String): Source {
            return Source.newBuilder(LANGUAGE_ID, script, "<internal-script>").internal(true).buildLiteral()
        }

    }

}