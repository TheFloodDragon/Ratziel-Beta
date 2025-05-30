package cn.fd.ratziel.module.script.impl

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
     * 获取 [ScriptEngine]
     */
    abstract fun getEngine(): ScriptEngine

    override fun evalDirectly(script: String, environment: ScriptEnvironment): Any? {
        val engine = getEngine()
        return engine.eval(script, createContext(engine, environment))
    }

    override fun compile(script: String, environment: ScriptEnvironment): CompiledScript {
        val engine = getEngine()
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
        val context = ImportedScriptContext()

        // 导入环境的引擎绑定键
        val engineBindings = engine.createBindings() // 需要通过脚本引擎创建, 以便脚本内部上下文的继承
        engineBindings.putAll(environment.bindings)

        // 导入全局绑定键
        val globalBindings = environment.context.getBindings(ScriptContext.GLOBAL_SCOPE) // 导入环境的全局绑定键

        // 设置绑定键
        context.setBindings(engineBindings, ScriptContext.ENGINE_SCOPE)
        context.setBindings(globalBindings, ScriptContext.GLOBAL_SCOPE)
        return context
    }

}