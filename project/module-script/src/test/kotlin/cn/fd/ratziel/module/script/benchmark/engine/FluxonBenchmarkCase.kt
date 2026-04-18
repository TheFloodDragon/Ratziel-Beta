package cn.fd.ratziel.module.script.benchmark.engine

import cn.fd.ratziel.module.script.benchmark.BenchmarkCase
import cn.fd.ratziel.module.script.benchmark.ScriptSample
import cn.fd.ratziel.module.script.benchmark.engineSamples
import cn.fd.ratziel.module.script.api.CompiledScript
import cn.fd.ratziel.module.script.api.ScriptEnvironment
import cn.fd.ratziel.module.script.api.ScriptSource
import cn.fd.ratziel.module.script.conf.ScriptConfiguration
import cn.fd.ratziel.module.script.conf.ScriptConfigurationKeys
import cn.fd.ratziel.module.script.conf.scriptCaching
import cn.fd.ratziel.module.script.lang.fluxon.FluxonLang
import cn.fd.ratziel.module.script.lang.fluxon.FluxonScriptExecutor

internal object FluxonBenchmarkCase : BenchmarkCase<FluxonPreparedScript> {

    private val configuration = ScriptConfiguration {
        this[ScriptConfigurationKeys.scriptCaching] = 2
    }

    private val executor = FluxonScriptExecutor().apply {
        this.configuration = this@FluxonBenchmarkCase.configuration
    }

    override val engineName: String = "Fluxon"

    override val samples: Map<String, ScriptSample> = engineSamples("fluxon")

    override fun prepare(sample: ScriptSample): FluxonPreparedScript {
        val environment = ScriptEnvironment(
            bindings = sample.bindingsFactory(),
            configuration = configuration,
        )
        val source = ScriptSource.literal(
            sample.content,
            FluxonLang,
            sample.path.substringAfterLast('/').replace(Regex("[^A-Za-z0-9_]"), "_"),
        )
        val script = executor.compile(source, environment)
        return FluxonPreparedScript(script, environment)
    }

    override fun execute(prepared: FluxonPreparedScript): Any? {
        return prepared.script.eval(prepared.environment)
    }

}

internal data class FluxonPreparedScript(
    val script: CompiledScript,
    val environment: ScriptEnvironment,
)
