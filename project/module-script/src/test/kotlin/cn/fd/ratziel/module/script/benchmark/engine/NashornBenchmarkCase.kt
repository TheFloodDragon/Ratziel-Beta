package cn.fd.ratziel.module.script.benchmark.engine

import cn.fd.ratziel.module.script.benchmark.BenchmarkCase
import cn.fd.ratziel.module.script.benchmark.ScriptSample
import cn.fd.ratziel.module.script.benchmark.engineSamples
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory
import javax.script.Compilable
import javax.script.CompiledScript
import javax.script.ScriptContext
import javax.script.ScriptEngine
import javax.script.SimpleBindings

internal object NashornBenchmarkCase : BenchmarkCase<NashornPreparedScript> {

    private val factory = NashornScriptEngineFactory()

    override val engineName: String = "Nashorn"

    override val samples: Map<String, ScriptSample> = engineSamples("javascript", ".js")

    override fun compile(sample: ScriptSample): NashornPreparedScript {
        val engine = newEngine()
        engine.setBindings(SimpleBindings(sample.bindingsFactory()), ScriptContext.GLOBAL_SCOPE)
        val compiled = (engine as Compilable).compile(sample.content)
        return NashornPreparedScript(engine, compiled)
    }

    override fun runCompiled(compiled: NashornPreparedScript): Any? {
        return compiled.script.eval(compiled.engine.context)
    }

    override fun interpret(sample: ScriptSample): Any? {
        val engine = newEngine()
        engine.setBindings(SimpleBindings(sample.bindingsFactory()), ScriptContext.GLOBAL_SCOPE)
        return engine.eval(sample.content)
    }

    private fun newEngine(): ScriptEngine {
        return factory.getScriptEngine(
            arrayOf("-Dnashorn.args=--language=es6"),
            this::class.java.classLoader,
        )
    }
}

internal data class NashornPreparedScript(
    val engine: ScriptEngine,
    val script: CompiledScript,
)
