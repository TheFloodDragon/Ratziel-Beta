package cn.fd.ratziel.module.script.lang.js

import cn.fd.ratziel.module.script.api.ScriptEnvironment
import cn.fd.ratziel.module.script.api.ScriptSource
import cn.fd.ratziel.module.script.impl.CompileDefault
import cn.fd.ratziel.module.script.impl.EnginedScriptExecutor
import cn.fd.ratziel.module.script.imports.GroupImports
import org.graalvm.polyglot.Context
import org.graalvm.polyglot.Engine
import org.graalvm.polyglot.Source
import java.util.function.Supplier
import javax.script.ScriptEngine


/**
 * GraalJsScriptExecutor
 *
 * 其JSR223的实现完全限制了 GraalJs 的发挥, 所以自此不再在此使用 [ScriptEngine]
 *
 * @author TheFloodDragon
 * @since 2025/4/26 09:56
 */
object GraalJsScriptExecutor : EnginedScriptExecutor<Source, Context>(), CompileDefault {

    private const val LANGUAGE_ID = "js"
    private const val IMPORTS_MEMBER = "_imports_"

    /**
     * 依照 GraalVM Polyglot 的文档, 代码缓存的数据作为 [Engine] 实例的一部分.
     * 使用全局的 [Engine] 进行代码缓存或许可以提升效率.
     */
    val globalEngine: Engine = Engine.newBuilder(LANGUAGE_ID)
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
        .engine(globalEngine) // 绑定全局的 Engine
        .build()

    override fun evalDirectly(source: ScriptSource, environment: ScriptEnvironment): Any? {
        // 创建运行上下文
        val runtime = preheat(environment)
        // 评估脚本源 (不缓存)
        return runtime.eval(createSource(source, false)).`as`(Any::class.java)
    }

    override fun compile(source: ScriptSource, environment: ScriptEnvironment): CachedScript<Source, Context> {
        return CachedScript(createSource(source), environment, this)
    }

    override fun evalCompiled(compiled: CachedScript<Source, Context>, environment: ScriptEnvironment): Any? {
        // 获取当前线程的运行上下文
        val runtime = getRuntime(environment) { compiled.get() }
        return runtime.eval(compiled.script).`as`(Any::class.java)
    }

    override fun preheat(environment: ScriptEnvironment): Context {
        // 初始化预热上下文
        val context = getRuntime(environment) { newContext() }

        // 导入环境的导入组 (类、包、脚本)
        val imports = GroupImports.catcher[environment.context]

        // 导入类、包
        if (imports.classes.isNotEmpty() || imports.packages.isNotEmpty()) {
            // 导入脚本: 用于在运行时获取类、包
            context.eval(importingScript)
            // 设置成员: 供导入脚本在运行时获取类、包 (外部传入 GroupImports)
            val contextBindings = context.getBindings(LANGUAGE_ID)
            contextBindings.putMember(IMPORTS_MEMBER, imports)
        }

        // 导入脚本
        val scriptImports = imports.scripts(JavaScriptLang)
        if (scriptImports.isNotEmpty()) {
            for (import in scriptImports) {
                this.evaluate(import.compiled ?: continue, environment)
            }
        }

        return context
    }

    /**
     * 获取供脚本运行的上下文, 并导入环境的绑定键
     */
    @JvmStatic
    fun getRuntime(environment: ScriptEnvironment, init: Supplier<Context>): Context {
        // 获取环境中的上下文 (创建)
        val context = environment.context.fetch(this, init)
        // 导入环境的绑定键
        val environmentBindings = environment.bindings
        val contextBindings = context.getBindings(LANGUAGE_ID)
        if (environmentBindings.isNotEmpty()) {
            for ((key, value) in environmentBindings) {
                contextBindings.putMember(key, value)
            }
        }
        return context
    }

    /** 用来导入包和类的脚本 **/
    @JvmStatic
    private val importingScript by lazy {
        internalSource(this::class.java.classLoader.getResourceAsStream("internal/graaljs.importer.js")!!.reader().readText())
    }

    @JvmStatic
    private fun createSource(source: ScriptSource, cached: Boolean = true): Source {
        return Source.newBuilder(LANGUAGE_ID, source.content, "<eval>").cached(cached).build()
    }

    /** 编译内部脚本源 **/
    @JvmStatic
    private fun internalSource(script: String): Source {
        return Source.newBuilder(LANGUAGE_ID, script, "<internal-script>").internal(true).buildLiteral()
    }

}