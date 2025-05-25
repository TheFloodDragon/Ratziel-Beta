package cn.fd.ratziel.module.script.lang

import cn.fd.ratziel.module.script.ScriptManager
import cn.fd.ratziel.module.script.api.ScriptEnvironment
import cn.fd.ratziel.module.script.impl.EnginedScriptExecutor
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
object GraalJsScriptExecutor : EnginedScriptExecutor() {

    init {
        ScriptManager.loadDependencies("graaljs")
    }

    /**
     * 创建一个新的 [Context.Builder]
     */
    val builder by lazy {
        Context.newBuilder("js")
            .hostClassLoader(this::class.java.classLoader)
            .allowAllAccess(true) // 全开了算了
            .allowExperimentalOptions(true)
            .option("js.nashorn-compat", "true") // Nashorn 兼容模式
    }

    override fun getEngine(): ScriptEngine {
        return GraalJSScriptEngine.create(null, builder)
    }

    override fun createContext(engine: ScriptEngine, environment: ScriptEnvironment): ScriptContext {
        val context = super.createContext(engine, environment)
        // 导入默认
        JavaScriptExecutor.importDefaults(engine, context)
        return context
    }

}