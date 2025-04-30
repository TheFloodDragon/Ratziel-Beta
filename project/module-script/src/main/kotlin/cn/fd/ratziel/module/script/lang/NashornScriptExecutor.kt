package cn.fd.ratziel.module.script.lang

import cn.fd.ratziel.module.script.ScriptManager
import cn.fd.ratziel.module.script.api.ScriptEnvironment
import cn.fd.ratziel.module.script.internal.EnginedScriptExecutor
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory
import javax.script.ScriptEngine

/**
 * NashornScriptExecutor
 *
 * @author TheFloodDragon
 * @since 2025/4/26 09:33
 */
class NashornScriptExecutor(private val options: Array<String>) : EnginedScriptExecutor() {

    init {
        ScriptManager.loadDependencies("nashorn")
    }

    val scriptEngineFactory by lazy {
        ScriptManager.engineManager.engineFactories.find {
            it.engineName == "OpenJDK Nashorn"
        } as? NashornScriptEngineFactory
    }

    override fun newEngine(environment: ScriptEnvironment): ScriptEngine {
        val engine = scriptEngineFactory?.getScriptEngine(*options)
            ?: throw NullPointerException("Cannot find ScriptEngine for JavaScript Language")
        engine.context = environment.context // 使用环境上下文
        JavaScriptExecutor.importDefaults(engine) // 导入默认
        return engine
    }

}