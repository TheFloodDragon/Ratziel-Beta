package cn.fd.ratziel.module.script.lang

import cn.fd.ratziel.module.script.ScriptManager
import cn.fd.ratziel.module.script.api.ScriptEnvironment
import cn.fd.ratziel.module.script.impl.CompletableScriptExecutor
import cn.fd.ratziel.module.script.impl.ImportedScriptContext
import org.apache.commons.jexl3.JexlBuilder
import org.apache.commons.jexl3.JexlContext
import org.apache.commons.jexl3.JexlEngine
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

    val engine: JexlEngine by lazy {
        JexlBuilder().apply {
            loader(this::class.java.classLoader)
        }.create()
    }

    override fun evalDirectly(script: String, environment: ScriptEnvironment): Any? {
        return engine.createScript(script).execute(WrappedJexlContext(environment.context))
    }

    override fun compile(script: String, environment: ScriptEnvironment): JexlScript {
        return engine.createScript(script)
    }

    override fun evalCompiled(script: JexlScript, environment: ScriptEnvironment): Any? {
        // 创建上下文
        val context = WrappedJexlContext(environment.context)
        // 执行脚本
        return script.execute(context)
    }

    /**
     * 封装的 [JexlContext]
     */
    class WrappedJexlContext(context: ScriptContext) : JexlContext {

        val scriptContext = ImportedScriptContext(context)

        override fun get(name: String): Any? {
            return scriptContext.getAttribute(name)
        }

        override fun has(name: String): Boolean {
            return scriptContext.getAttributesScope(name) != -1
        }

        override fun set(name: String, value: Any?) {
            this.scriptContext.setAttribute(name, value, ScriptContext.ENGINE_SCOPE)
        }

    }

}