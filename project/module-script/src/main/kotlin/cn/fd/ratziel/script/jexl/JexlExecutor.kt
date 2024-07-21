package cn.fd.ratziel.script.jexl

import cn.fd.ratziel.script.api.CompilableScriptExecutor
import cn.fd.ratziel.script.api.ScriptContent
import cn.fd.ratziel.script.api.ScriptEnvironment
import taboolib.common.env.RuntimeDependency
import taboolib.common.platform.Ghost
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
@Ghost // 避免依赖注入, 主动触发
@RuntimeDependency(
    value = "!org.apache.commons:commons-jexl3:3.4.0",
    test = "!org.apache.commons.jexl3.JexlEngine",
    transitive = false
)
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