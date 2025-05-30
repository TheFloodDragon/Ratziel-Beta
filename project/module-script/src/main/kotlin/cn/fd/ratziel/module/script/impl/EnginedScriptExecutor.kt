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
     * 创建 [ScriptEngine]
     */
    abstract fun newEngine(): ScriptEngine

    /**
     * 获取 [ScriptEngine]
     */
    open fun getEngine(): ScriptEngine = newEngine()

    override fun evalDirectly(script: String, environment: ScriptEnvironment): Any? {
        val engine = getEngine()
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
     */
    @Synchronized
    open fun createContext(engine: ScriptEngine, environment: ScriptEnvironment): ScriptContext {
        return environment.context
    }

}