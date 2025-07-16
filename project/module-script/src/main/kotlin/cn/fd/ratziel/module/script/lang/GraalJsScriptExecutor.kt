package cn.fd.ratziel.module.script.lang

import cn.fd.ratziel.module.script.api.ScriptEnvironment
import cn.fd.ratziel.module.script.impl.EnginedScriptExecutor
import cn.fd.ratziel.module.script.internal.NonStrictCompilation
import com.oracle.truffle.js.scriptengine.GraalJSScriptEngine
import org.graalvm.polyglot.Context
import org.graalvm.polyglot.proxy.ProxyObject
import javax.script.ScriptContext
import javax.script.ScriptEngine

/**
 * GraalJsScriptExecutor
 *
 * @author TheFloodDragon
 * @since 2025/4/26 09:56
 */
object GraalJsScriptExecutor : EnginedScriptExecutor(), NonStrictCompilation {

    /**
     * 创建一个新的 [Context.Builder]
     */
    val builder: Context.Builder by lazy {
        Context.newBuilder("js")
            .hostClassLoader(this::class.java.classLoader)
            .allowAllAccess(true) // 全开了算了
            .allowExperimentalOptions(true)
            .option("js.ecmascript-version", "latest")
            .option("js.nashorn-compat", "true") // Nashorn 兼容模式
    }

    override fun newEngine(): ScriptEngine {
        val engine = GraalJSScriptEngine.create(null, builder)
        // 导入要导入的包和类
        engine.eval(importingScript)
        // 加载全局扩展脚本
        for (script in JavaScriptLang.globalScripts) {
            engine.eval(script)
        }
        return engine
    }

    @Synchronized
    override fun createContext(engine: ScriptEngine, environment: ScriptEnvironment): ScriptContext {
        val context = super.createContext(engine, environment)
        // GraalJS 导入全局类是从 全局域 导入到 引擎域, 所以此处需要重新导入下 (手动导入)
        val proxy = ProxyObject.fromMap(context.getBindings(ScriptContext.ENGINE_SCOPE))
        (engine as GraalJSScriptEngine).polyglotContext.getBindings("js").getMember("importScriptEngineGlobalBindings").execute(proxy)
        return context
    }

    /**
     * 用来导入包和类的脚本
     */
    private val importingScript by lazy {
        this::class.java.classLoader.getResourceAsStream("script-default/graaljs.scriptengine.js")!!.reader().readText()
    }

}