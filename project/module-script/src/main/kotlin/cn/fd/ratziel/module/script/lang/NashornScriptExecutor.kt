package cn.fd.ratziel.module.script.lang

import cn.fd.ratziel.module.script.ScriptManager
import cn.fd.ratziel.module.script.impl.EnginedScriptExecutor
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory
import org.openjdk.nashorn.internal.objects.NativeJava
import javax.script.ScriptContext
import javax.script.ScriptEngine

/**
 * NashornScriptExecutor
 *
 * @author TheFloodDragon
 * @since 2025/4/26 09:33
 */
object NashornScriptExecutor : EnginedScriptExecutor() {

    init {
        ScriptManager.loadDependencies("nashorn")
    }

    val scriptEngineFactory by lazy {
        ScriptManager.engineManager.engineFactories.find {
            it.engineName == "OpenJDK Nashorn"
        } as? NashornScriptEngineFactory
    }

    val globalEngine: ScriptEngine by lazy {
        val engine = createEngine()
        val globalBindings = engine.createBindings().apply {
            for (cls in ScriptManager.Global.classes) {
                put(
                    cls.substringAfterLast('.'),
                    NativeJava.type(null, "'$cls'")
                )
            }
            for (pkg in ScriptManager.Global.packages) {
                put(
                    pkg.substringAfterLast('.'),
                    NativeJava.type(null, "Packages.$pkg")
                )
            }
        }
        engine.setBindings(globalBindings, ScriptContext.GLOBAL_SCOPE)
        engine
    }

    override fun getEngine() = globalEngine

    /**
     * 创建脚本引擎实例
     */
    fun createEngine(): ScriptEngine {
        val engine = scriptEngineFactory?.getScriptEngine(
            arrayOf("-Dnashorn.args=--language=es6"), this::class.java.classLoader
        ) ?: throw NullPointerException("Cannot find ScriptEngine for JavaScript(Nashorn) Language")
        return engine
    }

}