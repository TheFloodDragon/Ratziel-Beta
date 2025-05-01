package cn.fd.ratziel.module.script.lang

import cn.fd.ratziel.module.script.ScriptManager
import cn.fd.ratziel.module.script.internal.EnginedScriptExecutor
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory
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

    override fun newEngine(): ScriptEngine {
        val engine = scriptEngineFactory?.getScriptEngine(
            "--language=es6",
        ) ?: throw NullPointerException("Cannot find ScriptEngine for JavaScript(Nashorn) Language")
        JavaScriptExecutor.importDefaults(engine) // 导入默认
        return engine
    }

}