package cn.fd.ratziel.module.script.lang.js

import cn.fd.ratziel.module.script.ScriptType
import cn.fd.ratziel.module.script.api.ScriptEnvironment
import cn.fd.ratziel.module.script.impl.EnginedScriptExecutor
import cn.fd.ratziel.module.script.imports.GroupImports
import cn.fd.ratziel.module.script.internal.NonStrictCompilation
import org.graalvm.polyglot.Context
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
object GraalJsScriptExecutor : EnginedScriptExecutor<Source>(), NonStrictCompilation {

    private const val LANGUAGE_ID = "js"
    private const val IMPORTS_MEMBER = "_imports_"

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
     * 创建一个新的 [Context]
     */
    @JvmStatic
    fun newContext(): Context {
        val context = builder.build()
        // 包、类导入脚本
        context.eval(importingScript)
        return context
    }

    override fun evalDirectly(script: String, environment: ScriptEnvironment): Any? {
        return getContext(environment).eval(createSource(script)).`as`(Any::class.java)
    }

    override fun compile(script: String, environment: ScriptEnvironment): Source {
        return createSource(script)
    }

    override fun evalCompiled(script: Source, environment: ScriptEnvironment): Any? {
        return getContext(environment).eval(script).`as`(Any::class.java)
    }

    override fun preheat(environment: ScriptEnvironment) {
        val imports = GroupImports.catcher[environment.context]
        if (imports.isEmpty()) return
        // 获取脚本执行要的上下文
        val context = getContext(environment)
        // 设置导入的类成员
        val contextBindings = context.getBindings(LANGUAGE_ID)
        contextBindings.putMember(IMPORTS_MEMBER, imports)
        // 导入脚本
        val scriptImports = imports.scripts[ScriptType.JAVASCRIPT].orEmpty()
        for (import in scriptImports) {
            this.evaluate(import.compiled ?: continue, environment)
        }
    }

    /**
     * 创建供脚本运行的上下文
     */
    fun getContext(environment: ScriptEnvironment): Context {
        // 获取执行器上下文 (上下文继承)
        val context = environment.context.fetch(this) { newContext() }
        val environmentBindings = environment.bindings // 环境绑定
        val contextBindings = context.getBindings(LANGUAGE_ID)
        // 导入环境上下文
        if (environmentBindings.isNotEmpty()) {
            for ((key, value) in environmentBindings) {
                contextBindings.putMember(key, value)
            }
        }
        contextBindings.putMember("bindings", environmentBindings)
        return context
    }

    /** 用来导入包和类的脚本 **/
    private val importingScript by lazy {
        internalSource(this::class.java.classLoader.getResourceAsStream("internal/graaljs.importer.js")!!.reader().readText())
    }

    private fun createSource(script: String): Source {
        return Source.newBuilder(LANGUAGE_ID, script, "<eval>").build()
    }

    /** 编译内部脚本源 **/
    private fun internalSource(script: String): Source {
        return Source.newBuilder(LANGUAGE_ID, script, "<internal-script>").internal(true).buildLiteral()
    }

}