package cn.fd.ratziel.module.script.internal

import cn.fd.ratziel.module.script.api.ScriptEnvironment
import javax.script.Compilable
import javax.script.CompiledScript
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
        return newEngine().eval(script, environment.context)
    }

    override fun compile(script: String): CompiledScript {
        return (this.newEngine() as Compilable).compile(script)
    }

    override fun evalCompiled(script: CompiledScript, environment: ScriptEnvironment): Any? {
        return script.eval(environment.context)
    }

}