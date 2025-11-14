package cn.fd.ratziel.module.script.lang.fluxon

import cn.fd.ratziel.module.script.api.*
import org.tabooproject.fluxon.Fluxon
import org.tabooproject.fluxon.parser.ParseResult
import org.tabooproject.fluxon.runtime.FluxonRuntime
import org.tabooproject.fluxon.runtime.Environment


/**
 * FluxonScriptExecutor
 *
 * @author TheFloodDragon
 * @since 2025/11/1 22:06
 */
class FluxonScriptExecutor : IntegratedScriptExecutor() {

    override fun compile(source: ScriptSource, environment: ScriptEnvironment): CompiledScript {
        return object : ValuedCompiledScript<List<ParseResult>>(
            Fluxon.parse(source.content, environment.asFluxonEnv()),
            source, this
        ) {
            override fun eval(environment: ScriptEnvironment): Any? {
                return Fluxon.eval(script, environment.asFluxonEnv())
            }
        }
    }

    override fun evaluate(script: ScriptContent, environment: ScriptEnvironment): Any? {
        return Fluxon.eval(script.content, environment.asFluxonEnv())
    }

    fun ScriptEnvironment.asFluxonEnv(): Environment {
        return FluxonRuntime.getInstance().newEnvironment()
    }

    override fun compiler() = FluxonScriptExecutor()
    override fun evaluator() = FluxonScriptExecutor()

    companion object {

        /**
         * 默认脚本实例
         */
        @JvmField
        val DEFAULT = FluxonScriptExecutor()

    }

}