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

    override fun prepare(sample: ScriptSample): NashornPreparedScript {
        val engine = newEngine()
        engine.setBindings(SimpleBindings(sample.bindingsFactory()), ScriptContext.GLOBAL_SCOPE)
        val compiled = (engine as Compilable).compile(sample.content)
        return NashornPreparedScript(compiled, engine.context)
    }

    /** 热执行：纯 `CompiledScript.eval(ScriptContext)`，context 在 [prepare] 里已缓存。 */
    override fun execute(prepared: NashornPreparedScript): Any? {
        return prepared.script.eval(prepared.context)
    }

    override fun evaluate(sample: ScriptSample): Any? {
        val engine = newEngine()
        engine.setBindings(SimpleBindings(sample.bindingsFactory()), ScriptContext.GLOBAL_SCOPE)
        return engine.eval(sample.content)
    }

    fun newEngine(): ScriptEngine = factory.getScriptEngine(
        arrayOf("-Dnashorn.args=--language=es6"),
        this::class.java.classLoader,
    )
}

internal data class NashornPreparedScript(
    val script: CompiledScript,
    val context: ScriptContext,
)
