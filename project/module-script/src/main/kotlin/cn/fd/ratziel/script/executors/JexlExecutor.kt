package cn.fd.ratziel.script.executors

import cn.fd.ratziel.script.api.ScriptContent
import cn.fd.ratziel.script.api.ScriptEnvironment
import cn.fd.ratziel.script.impl.CompilableScriptExecutor
import javax.script.Compilable
import javax.script.CompiledScript
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager

/**
 * JexlExecutor
 *
 * @author TheFloodDragon
 * @since 2024/7/14 21:41
 */
object JexlExecutor : CompilableScriptExecutor {

    override fun compile(script: String?): CompiledScript {
        return (newEngine() as Compilable).compile(script)
    }

    override fun evaluate(script: ScriptContent, environment: ScriptEnvironment): Any? {
        return newEngine().eval(script.content, environment.bindings)
    }

    fun newEngine(): ScriptEngine =
        ScriptEngineManager(this::class.java.classLoader).getEngineByName("Jexl")
            ?: throw NullPointerException("Cannot find ScriptEngine for JexlExecutor")

}