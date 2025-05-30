package cn.fd.ratziel.module.script.lang

import cn.fd.ratziel.module.script.ScriptManager
import cn.fd.ratziel.module.script.impl.EnginedScriptExecutor
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
    val builder: Context.Builder by lazy {
        Context.newBuilder("js")
            .hostClassLoader(this::class.java.classLoader)
            .allowAllAccess(true) // 全开了算了
            .allowExperimentalOptions(true)
            .option("js.nashorn-compat", "true") // Nashorn 兼容模式
    }

    val globalEngine: ScriptEngine by lazy {
        val engine = GraalJSScriptEngine.create(null, builder)
        JavaScriptExecutor.initGlobal(engine)
    }

    override fun getEngine() = globalEngine

}