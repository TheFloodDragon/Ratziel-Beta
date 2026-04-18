package cn.fd.ratziel.module.script.performance

import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory
import javax.script.Compilable
import javax.script.CompiledScript
import javax.script.ScriptEngine

internal object NashornBenchmarkCase : BenchmarkCase<NashornPreparedScript> {

    private val factory = NashornScriptEngineFactory()

    override val engineName: String = "Nashorn (JSR223)"

    override val dialect: ScriptDialect = ScriptDialect.JavaScript

    override val samples: Map<String, ScriptSample> = engineSamples(dialect)

    override fun prepare(sample: ScriptSample): NashornPreparedScript {
        val engine = factory.getScriptEngine(
            arrayOf("-Dnashorn.args=--language=es6"),
            this::class.java.classLoader,
        )
        val compiled = (engine as Compilable).compile(sample.content)
        return NashornPreparedScript(engine, compiled)
    }

    override fun execute(prepared: NashornPreparedScript): Any? {
        return prepared.script.eval(prepared.engine.context)
    }

}

internal data class NashornPreparedScript(
    val engine: ScriptEngine,
    val script: CompiledScript,
)
