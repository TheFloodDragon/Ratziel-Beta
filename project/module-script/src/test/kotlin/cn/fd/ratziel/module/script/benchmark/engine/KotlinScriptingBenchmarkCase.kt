package cn.fd.ratziel.module.script.benchmark.engine

import cn.fd.ratziel.module.script.benchmark.BenchmarkCase
import cn.fd.ratziel.module.script.benchmark.ScriptSample
import cn.fd.ratziel.module.script.benchmark.engineSamples
import cn.fd.ratziel.module.script.lang.kts.KtsScriptEngineFactory
import javax.script.Compilable
import javax.script.CompiledScript
import javax.script.ScriptContext
import javax.script.ScriptEngine
import javax.script.SimpleBindings

internal object KotlinScriptingBenchmarkCase : BenchmarkCase<KotlinPreparedScript> {

    override val engineName: String = "KotlinScripting"

    override val samples: Map<String, ScriptSample> = engineSamples("kotlin")

    override fun prepare(sample: ScriptSample): KotlinPreparedScript {
        val engine = KtsScriptEngineFactory.getScriptEngine()
        engine.setBindings(SimpleBindings(sample.bindingsFactory()), ScriptContext.ENGINE_SCOPE)
        val compiled = (engine as Compilable).compile(sample.content)
        return KotlinPreparedScript(engine, compiled)
    }

    override fun execute(prepared: KotlinPreparedScript): Any? {
        return prepared.script.eval(prepared.engine.context)
    }

}

internal data class KotlinPreparedScript(
    val engine: ScriptEngine,
    val script: CompiledScript,
)
