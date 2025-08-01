package cn.fd.ratziel.module.script.lang.js

import cn.fd.ratziel.module.script.api.ScriptEnvironment
import cn.fd.ratziel.module.script.impl.EnginedScriptExecutor
import cn.fd.ratziel.module.script.internal.NonStrictCompilation
import cn.fd.ratziel.module.script.lang.JavaScriptLang
import com.oracle.truffle.js.scriptengine.GraalJSScriptEngine
import org.graalvm.polyglot.Context
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

    override fun createContext(engine: ScriptEngine, environment: ScriptEnvironment): ScriptContext {
        val globalBindings = engine.context.getBindings(ScriptContext.GLOBAL_SCOPE)
        val engineBindings = engine.context.getBindings(ScriptContext.ENGINE_SCOPE)
        // 清除原有的环境导入
        for ((key, _) in globalBindings) engineBindings?.remove(key)
        // 创建上下文 (导入环境到全局域)
        val context = super.createContext(engine, environment)
        // GraalJS 导入全局类是从 全局域 导入到 引擎域, 所以此处需要重新导入下环境 (手动导入)
        engineBindings.putAll(globalBindings)
        return context
    }

    /**
     * 用来导入包和类的脚本
     */
    private val importingScript by lazy {
        this::class.java.classLoader.getResourceAsStream("script-default/graaljs.scriptengine.js")!!.reader().readText()
    }

}