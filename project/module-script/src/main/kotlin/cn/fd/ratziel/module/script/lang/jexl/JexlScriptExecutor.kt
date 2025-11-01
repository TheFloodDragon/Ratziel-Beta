package cn.fd.ratziel.module.script.lang.jexl

import cn.fd.ratziel.module.script.api.*
import cn.fd.ratziel.module.script.impl.ImportedScriptContext
import org.apache.commons.jexl3.JexlBuilder
import org.apache.commons.jexl3.JexlContext
import org.apache.commons.jexl3.JexlEngine
import org.apache.commons.jexl3.JexlScript
import javax.script.ScriptContext
import javax.script.SimpleBindings

/**
 * JexlScriptExecutor
 *
 * @author TheFloodDragon
 * @since 2025/4/25 17:37
 */
class JexlScriptExecutor : IntegratedScriptExecutor() {

    /**
     * Jexl引擎实例
     */
    val engine: JexlEngine by lazy {
        JexlBuilder().apply {
            loader(this::class.java.classLoader)
        }.create()
    }

    override fun compile(source: ScriptSource, environment: ScriptEnvironment): CompiledScript {
        // 创建Jexl脚本
        val jexlScript = when (source) {
            is FileScriptSource -> engine.createScript(source.file)
            is LiteralScriptSource -> engine.createScript(source.content)
        }
        // 返回封装后的脚本
        return object : ValuedCompiledScript<JexlScript>(jexlScript, source, this) {
            override fun eval(environment: ScriptEnvironment): Any? {
                return script.execute(WrappedJexlContext(environment))
            }
        }
    }

    override fun evaluate(script: ScriptContent, environment: ScriptEnvironment): Any? {
        return engine.createExpression(script.source.content)
            .evaluate(WrappedJexlContext(environment))
    }

    /**
     * 封装的 [JexlContext]
     */
    class WrappedJexlContext(val context: ImportedScriptContext) : JexlContext {

        constructor(environment: ScriptEnvironment) : this(
            ImportedScriptContext().apply {
                setBindings(SimpleBindings(environment.bindings), ScriptContext.ENGINE_SCOPE)
            }
        )

        override fun get(name: String): Any? = context.getAttribute(name)
        override fun has(name: String) = context.getAttributesScope(name) != -1
        override fun set(name: String, value: Any?) = context.setAttribute(name, value, ScriptContext.ENGINE_SCOPE)

    }

    override fun compiler() = JexlScriptExecutor()
    override fun evaluator() = JexlScriptExecutor()

    companion object {

        /**
         * 默认脚本执行器实例
         */
        @JvmField
        val DEFAULT = JexlScriptExecutor()

    }

}