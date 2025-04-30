package cn.fd.ratziel.module.script.lang

import cn.fd.ratziel.module.script.ScriptManager
import cn.fd.ratziel.module.script.api.ScriptEnvironment
import cn.fd.ratziel.module.script.internal.EnginedScriptExecutor
import com.oracle.truffle.js.scriptengine.GraalJSScriptEngine
import org.graalvm.polyglot.Context
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
            .allowAllAccess(true) // 全开了算了
            .allowExperimentalOptions(true)
            .option("js.nashorn-compat", "true") // Nashorn 兼容模式
    }

    override fun newEngine(environment: ScriptEnvironment): ScriptEngine {
        val engine = GraalJSScriptEngine.create(null, builder)
        engine.context = environment.context // 使用环境上下文
        JavaScriptExecutor.importDefaults(engine) // 导入默认
        return engine
    }

}