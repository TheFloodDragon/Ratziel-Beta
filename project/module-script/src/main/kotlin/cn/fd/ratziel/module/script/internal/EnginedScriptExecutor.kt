package cn.fd.ratziel.module.script.internal

import cn.fd.ratziel.module.script.api.ScriptEnvironment
import javax.script.Compilable
import javax.script.CompiledScript
import javax.script.ScriptContext
import javax.script.ScriptEngine

/**
 * EnginedScriptExecutor
 *
 * @author TheFloodDragon
 * @since 2025/4/25 16:40
 */
abstract class EnginedScriptExecutor : CompletableScriptExecutor<CompiledScript>() {

    /**
     * 创建一个新的 [ScriptEngine]
     */
    abstract fun newEngine(): ScriptEngine

    override fun evalDirectly(script: String, environment: ScriptEnvironment): Any? {
        val engine = newEngine()
        importBindings(engine, environment) // 导入环境的绑定键
        return engine.eval(script)
    }

    override fun compile(script: String): CompiledScript {
        return (this.newEngine() as Compilable).compile(script)
    }

    override fun evalCompiled(script: CompiledScript, environment: ScriptEnvironment): Any? {
        val engine = script.engine
        importBindings(engine, environment) // 导入环境的绑定键
        return script.eval(engine.context)
    }

    /**
     * 导入环境的绑定键
     * (为了避免引擎沾染环境, 所以要导入绑定键而不是直接用环境上下文)
     */
    private fun importBindings(engine: ScriptEngine, environment: ScriptEnvironment) {
        engine.context.getBindings(ScriptContext.ENGINE_SCOPE).putAll(environment.bindings)
    }

}