package cn.fd.ratziel.module.script.impl

import cn.fd.ratziel.module.script.api.ScriptEnvironment
import javax.script.*

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
        return engine.eval(script, createContext(engine, environment))
    }

    override fun compile(script: String, environment: ScriptEnvironment): CompiledScript {
        val engine = newEngine()
        engine.context = createContext(engine, environment)
        return (engine as Compilable).compile(script)
    }

    override fun evalCompiled(script: CompiledScript, environment: ScriptEnvironment): Any? {
        return script.eval(createContext(script.engine, environment))
    }

    /**
     * 创建 [ScriptContext]
     * (为了避免引擎沾染环境, 所以要导入绑定键而不是直接用环境上下文)
     */
    open fun createContext(engine: ScriptEngine, environment: ScriptEnvironment): ScriptContext {
        val context = SimpleScriptContext()
        // 导入环境的绑定键
        val engineBindings = engine.createBindings() // 需要通过脚本引擎创建, 以便脚本内部上下文的继承
        engineBindings.putAll(environment.bindings)
        context.setBindings(engineBindings, ScriptContext.ENGINE_SCOPE)
        // 导入全局绑定键
        val globalBindings = environment.context.getBindings(ScriptContext.GLOBAL_SCOPE)
        context.setBindings(globalBindings, ScriptContext.GLOBAL_SCOPE)
        return context
    }

}