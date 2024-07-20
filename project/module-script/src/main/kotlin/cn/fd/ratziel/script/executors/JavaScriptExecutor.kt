package cn.fd.ratziel.script.executors

import cn.fd.ratziel.script.api.ScriptContent
import cn.fd.ratziel.script.api.ScriptEnvironment
import cn.fd.ratziel.script.impl.CompilableScriptExecutor
import taboolib.common.env.RuntimeDependency
import javax.script.Compilable
import javax.script.CompiledScript
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager

/**
 * JavaScriptExecutor
 *
 * @author TheFloodDragon
 * @since 2024/7/14 21:40
 */
@RuntimeDependency(
    value = "!org.openjdk.nashorn:nashorn-core:15.4",
    test = "!org.openjdk.nashorn.api.scripting.NashornScriptEngine"
)
object JavaScriptExecutor : CompilableScriptExecutor {

    override fun compile(script: String?): CompiledScript {
        return (newEngine() as Compilable).compile(script)
    }

    override fun evaluate(script: ScriptContent, environment: ScriptEnvironment): Any? {
        return newEngine().eval(script.content, environment.bindings)
    }

    fun newEngine(): ScriptEngine =
        ScriptEngineManager(this::class.java.classLoader).getEngineByName("js")
            ?: throw NullPointerException("Cannot find ScriptEngine for JavaScriptExecutor")

}