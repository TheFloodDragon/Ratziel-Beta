package cn.fd.ratziel.module.script.lang

import cn.fd.ratziel.module.script.ScriptManager
import cn.fd.ratziel.module.script.internal.EnginedScriptExecutor
import cn.fd.ratziel.module.script.internal.Initializable
import org.apache.commons.jexl3.JexlBuilder
import org.apache.commons.jexl3.scripting.JexlScriptEngine
import taboolib.library.configuration.ConfigurationSection
import javax.script.ScriptEngine

/**
 * JexlScriptExecutor
 *
 * @author TheFloodDragon
 * @since 2025/4/25 17:37
 */
object JexlScriptExecutor : EnginedScriptExecutor(), Initializable {

    val builder by lazy {
        JexlBuilder().apply {
            imports(ScriptManager.globalImports)
        }
    }

    override fun newEngine(): ScriptEngine {
        val engine = ScriptManager.engineManager.getEngineByName("jexl")
            ?: throw NullPointerException("Cannot find ScriptEngine for Jexl Language")
        synchronized(JexlScriptExecutor::class) {
            JexlScriptEngine.setInstance(builder.create())
        }
        return engine
    }

    override fun initialize(settings: ConfigurationSection) {
        ScriptManager.loadDependencies("jexl")
    }

}