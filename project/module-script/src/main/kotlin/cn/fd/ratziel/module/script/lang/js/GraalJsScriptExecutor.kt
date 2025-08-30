package cn.fd.ratziel.module.script.lang.js

import cn.fd.ratziel.core.functional.replenish
import cn.fd.ratziel.module.script.api.ScriptEnvironment
import cn.fd.ratziel.module.script.impl.CompilableScriptExecutor
import cn.fd.ratziel.module.script.internal.NonStrictCompilation
import cn.fd.ratziel.module.script.lang.JavaScriptLang
import org.graalvm.polyglot.Context
import org.graalvm.polyglot.Source
import java.util.concurrent.CompletableFuture
import javax.script.ScriptEngine


/**
 * GraalJsScriptExecutor
 *
 * 其JSR223的实现完全限制了 GraalJs 的发挥, 所以自此不再在此使用 [ScriptEngine]
 *
 * @author TheFloodDragon
 * @since 2025/4/26 09:56
 */
object GraalJsScriptExecutor : CompilableScriptExecutor<GraalJsScriptExecutor.CachedSource>(), NonStrictCompilation {

    private const val LANGUAGE_ID = "js"

    /**
     * 默认的 [Context.Builder]
     */
    val builder: Context.Builder = Context.newBuilder("js")
        .hostClassLoader(this::class.java.classLoader)
        .allowAllAccess(true) // 全开了算了
        .allowExperimentalOptions(true)
        .option("js.ecmascript-version", "latest")
        .option("js.nashorn-compat", "true") // Nashorn 兼容模式

    /**
     * [Context] 补充器
     */
    private val initializingContext: CompletableFuture<Context> by replenish {
        CompletableFuture.supplyAsync { initialContext() }
    }

    /**
     * 创建一个初始的 [Context]
     */
    @JvmStatic
    fun initialContext(): Context {
        val context = builder.build()
        // 导入要导入的包和类
        context.eval(importingScript)
        // 加载全局扩展脚本
        for ((script, attached) in JavaScriptLang.globalScripts) {
            context.eval(attached.fetch(this) { internalSource(script) })
        }
        return context
    }

    override fun evalDirectly(script: String, environment: ScriptEnvironment): Any? {
        val context = createContext(environment) { this.initializingContext.get() }
        return context.eval(createSource(script)).`as`(Any::class.java)
    }

    override fun compile(script: String): CachedSource {
        return CachedSource(createSource(script))
    }

    override fun evalCompiled(script: CachedSource, environment: ScriptEnvironment): Any? {
        val context = createContext(environment) { script.initializingContext.get() }
        return context.eval(script.source).`as`(Any::class.java)
    }

    /**
     * 创建供脚本运行的上下文
     */
    fun createContext(environment: ScriptEnvironment, contextGetter: () -> Context): Context {
        // 获取执行器上下文 (上下文继承)
        val context = environment.context.fetch(this) { contextGetter() }
        val bindings = environment.bindings // 环境绑定
        val contextBindings = context.getBindings(LANGUAGE_ID)
        // 导入环境上下文
        if (bindings.isNotEmpty()) {
            for ((key, value) in bindings) {
                contextBindings.putMember(key, value)
            }
        }
        contextBindings.putMember("bindings", bindings)
        return context
    }

    /** 用来导入包和类的脚本 **/
    private val importingScript by lazy {
        internalSource(this::class.java.classLoader.getResourceAsStream("script-default/graaljs.scriptengine.js")!!.reader().readText())
    }

    private fun createSource(script: String): Source {
        return Source.newBuilder(LANGUAGE_ID, script, "<eval>").build()
    }

    /** 编译内部脚本源 **/
    private fun internalSource(script: String): Source {
        return Source.newBuilder(LANGUAGE_ID, script, "<internal-script>").internal(true).buildLiteral()
    }

    /**
     * 内部维护 [initializingContext] 以提高并行执行多编译脚本的性能
     */
    class CachedSource(val source: Source) {
        internal val initializingContext: CompletableFuture<Context> by replenish {
            CompletableFuture.supplyAsync { initialContext() }
        }
    }

}