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
     * 创建一个新的 [ScriptEngine]
     */
    abstract fun newEngine(): ScriptEngine

    override fun evalDirectly(script: String, environment: ScriptEnvironment): Any? {
        return newImportedEngine(environment).eval(script)
    }

    override fun compile(script: String, environment: ScriptEnvironment): CompiledScript {
        return (newImportedEngine(environment) as Compilable).compile(script)
    }

    override fun evalCompiled(script: CompiledScript, environment: ScriptEnvironment): Any? {
        val bindings = script.engine.createBindings()
        bindings.putAll(environment.bindings) // 导入环境的绑定键
        return script.eval(bindings)
    }

    /**
     * 导入环境的绑定键
     * (为了避免引擎沾染环境, 所以要导入绑定键而不是直接用环境上下文)
     */
    private fun newImportedEngine(environment: ScriptEnvironment): ScriptEngine {
        val engine = newEngine()
        // 导入环境的绑定键
        val engineBindings = engine.context.getBindings(ScriptContext.ENGINE_SCOPE)
        engineBindings.putAll(environment.bindings)
        // 导入全局绑定键
        val globalBindings = environment.context.getBindings(ScriptContext.GLOBAL_SCOPE)
        engine.context.setBindings(globalBindings, ScriptContext.GLOBAL_SCOPE)
        // 返回导入后的脚本引擎
        return engine
    }

}