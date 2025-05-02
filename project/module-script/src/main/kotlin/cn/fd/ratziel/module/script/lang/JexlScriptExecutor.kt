package cn.fd.ratziel.module.script.lang

import cn.fd.ratziel.module.script.ScriptManager
import cn.fd.ratziel.module.script.api.ScriptEnvironment
import cn.fd.ratziel.module.script.impl.CompletableScriptExecutor
import org.apache.commons.jexl3.JexlBuilder
import org.apache.commons.jexl3.JexlContext
import org.apache.commons.jexl3.JexlScript
import javax.script.ScriptContext

/**
 * JexlScriptExecutor
 *
 * @author TheFloodDragon
 * @since 2025/4/25 17:37
 */
object JexlScriptExecutor : CompletableScriptExecutor<JexlScript>() {

    init {
        ScriptManager.loadDependencies("jexl")
    }

    val engine by lazy {
        JexlBuilder().apply {
            loader(this::class.java.classLoader)
            imports(ScriptManager.globalImports)
        }.create()
    }

    override fun evalDirectly(script: String, environment: ScriptEnvironment): Any? {
        return engine.createScript(script).execute(WrappedJexlContext(environment.context))
    }

    override fun compile(script: String, environment: ScriptEnvironment): JexlScript {
        return engine.createScript(script)
    }

    override fun evalCompiled(script: JexlScript, environment: ScriptEnvironment): Any? {
        return script.execute(WrappedJexlContext(environment.context))
    }

    /**
     * 封装的 [JexlContext]
     */
    class WrappedJexlContext(val scriptContext: ScriptContext) : JexlContext {

        override fun get(name: String): Any? {
            return scriptContext.getAttribute(name)
        }

        override fun has(name: String): Boolean {
            return scriptContext.getBindings(ScriptContext.ENGINE_SCOPE).containsKey(name)
        }

        override fun set(name: String, value: Any) {
            var scope = scriptContext.getAttributesScope(name)
            if (scope == -1) { // not found, default to engine
                scope = ScriptContext.ENGINE_SCOPE
            }
            this.scriptContext.getBindings(scope).put(name, value)
        }

    }

}