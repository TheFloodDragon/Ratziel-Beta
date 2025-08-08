package cn.fd.ratziel.module.script.lang.js

import cn.fd.ratziel.module.script.api.ScriptEnvironment
import cn.fd.ratziel.module.script.impl.CompletableScriptExecutor
import cn.fd.ratziel.module.script.internal.NonStrictCompilation
import cn.fd.ratziel.module.script.lang.JavaScriptLang
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
object GraalJsScriptExecutor : CompletableScriptExecutor<GraalJsScriptExecutor.CompiledSource>(), NonStrictCompilation {

    private const val LANGUAGE_ID = "js"

    /**
     * 默认的 [Context.Builder]
     */
    val builder = newBuilder()

    /**
     * 创建一个新的 [Context.Builder]
     */
    @JvmStatic
    fun newBuilder(): Context.Builder = Context.newBuilder("js")
        .hostClassLoader(this::class.java.classLoader)
        .allowAllAccess(true) // 全开了算了
        .allowExperimentalOptions(true)
        .option("js.ecmascript-version", "latest")
        .option("js.nashorn-compat", "true") // Nashorn 兼容模式

    /**
     * 创建一个新的 [Engine]
     */
    @JvmStatic
    fun newEngine(): Engine = Engine.newBuilder()
        .allowExperimentalOptions(true)
        .option("js.ecmascript-version", "latest")
        .option("js.nashorn-compat", "true") // Nashorn 兼容模式
        .build()

    /**
     * 创建一个初始的 [Context]
     */
    @JvmStatic
    fun initialContext(builder: Context.Builder): Context {
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
        return createContext(this.builder, environment).eval(createSource(script)).`as`(Any::class.java)
    }

    override fun compile(script: String): CompiledSource {
        return CompiledSource(this.newEngine(), createSource(script))
    }

    override fun evalCompiled(script: CompiledSource, environment: ScriptEnvironment): Any? {
        val builder = newBuilder().engine(script.engine) // 使用脚本源的引擎构建器
        return createContext(builder, environment).eval(script.source).`as`(Any::class.java)
    }

    /**
     * 创建供脚本运行的上下文
     */
    fun createContext(builder: Context.Builder, environment: ScriptEnvironment): Context {
        // 获取执行器上下文 (上下文继承)
        val context = environment.attachedContext.fetch(this) {
            initialContext(builder) // 创建一个初始的上下文
        }
        val bindings = environment.bindings // 环境绑定
        // 导入环境上下文
        if (bindings.isNotEmpty()) {
            for ((key, value) in bindings) {
                context.polyglotBindings.putMember(key, value)
            }
        }
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
        return Source.newBuilder(LANGUAGE_ID, script, "internal-script").internal(true).buildLiteral()
    }

    /**
     * CompiledSource
     * @author TheFloodDragon
     * @since 2025/8/8 13:21
     */
    class CompiledSource(val engine: Engine, val source: Source)

}