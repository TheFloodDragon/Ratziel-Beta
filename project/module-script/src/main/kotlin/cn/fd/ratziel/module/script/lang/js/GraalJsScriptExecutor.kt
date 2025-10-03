package cn.fd.ratziel.module.script.lang.js

import cn.fd.ratziel.module.script.api.ScriptEnvironment
import cn.fd.ratziel.module.script.api.ScriptSource
import cn.fd.ratziel.module.script.impl.CompilableScriptExecutor
import cn.fd.ratziel.module.script.impl.CompileDefault
import cn.fd.ratziel.module.script.imports.GroupImports
import org.graalvm.polyglot.Context
import org.graalvm.polyglot.Source
import javax.script.ScriptEngine
import kotlin.concurrent.getOrSet


/**
 * GraalJsScriptExecutor
 *
 * 其JSR223的实现完全限制了 GraalJs 的发挥, 所以自此不再在此使用 [ScriptEngine]
 *
 * @author TheFloodDragon
 * @since 2025/4/26 09:56
 */
object GraalJsScriptExecutor : CompilableScriptExecutor<GraalJsScriptExecutor.SourceCache>, CompileDefault {

    private const val LANGUAGE_ID = "js"
    private const val IMPORTS_MEMBER = "_imports_"

    /**
     * 创建一个新的默认的 [Context.Builder]
     */
    fun newContext(): Context = Context.newBuilder(LANGUAGE_ID)
        .hostClassLoader(this::class.java.classLoader)
        .allowAllAccess(true) // 全开了算了
        .allowExperimentalOptions(true)
        .option("js.ecmascript-version", "latest")
        .option("js.nashorn-compat", "true") // Nashorn 兼容模式
        .build()

    override fun evalDirectly(source: ScriptSource, environment: ScriptEnvironment): Any? {
        // 创建运行上下文
        val runtime = environment.context.fetch(this) { initContext(environment) }
        // 评估脚本源 (不缓存)
        return runtime.eval(createSource(source, false)).`as`(Any::class.java)
    }

    override fun compile(source: ScriptSource, environment: ScriptEnvironment): SourceCache {
        return SourceCache(createSource(source))
    }

    override fun evalCompiled(compiled: SourceCache, environment: ScriptEnvironment): Any? {
        // 获取当前线程的运行上下文
        val runtime = compiled.local.getOrSet { initContext(environment) }
        return runtime.eval(compiled.source).`as`(Any::class.java)
    }

    /**
     * 初始化供脚本运行的上下文
     */
    @JvmStatic
    fun initContext(environment: ScriptEnvironment): Context {
        // 创建基本的上下文
        val context = newContext()

        // 导入环境的绑定键
        val environmentBindings = environment.bindings
        val contextBindings = context.getBindings(LANGUAGE_ID)
        if (environmentBindings.isNotEmpty()) {
            for ((key, value) in environmentBindings) {
                contextBindings.putMember(key, value)
            }
        }

        // 导入环境的导入组 (类、包、脚本)
        val imports = GroupImports.catcher[environment.context]

        // 导入类、包
        if (imports.classes.isNotEmpty() || imports.packages.isNotEmpty()) {
            // 导入脚本: 用于在运行时获取类、包
            context.eval(importingScript)
            // 设置成员: 供导入脚本在运行时获取类、包 (外部传入 GroupImports)
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

    /**
     * GraalJs 代码缓存
     */
    class SourceCache(
        val source: Source,
    ) {

        /**
         * [ThreadLocal] 存储每个线程的 [Context]
         * 同一个线程共享一个 [Context]
         */
        val local = ThreadLocal<Context>()

    }

}