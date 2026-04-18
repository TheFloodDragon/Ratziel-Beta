package cn.fd.ratziel.module.script.benchmark.engine

import cn.fd.ratziel.module.script.benchmark.BenchmarkCase
import cn.fd.ratziel.module.script.benchmark.ScriptSample
import cn.fd.ratziel.module.script.benchmark.engineSamples
import org.apache.commons.jexl3.JexlBuilder
import org.apache.commons.jexl3.JexlFeatures
import org.apache.commons.jexl3.JexlScript
import org.apache.commons.jexl3.MapContext
import org.apache.commons.jexl3.introspection.JexlPermissions

internal object JexlBenchmarkCase : BenchmarkCase<JexlPreparedScript> {

    private val engine = JexlBuilder()
        .cache(16)
        .strict(true)
        .features(JexlFeatures.createAll())
        .permissions(JexlPermissions.UNRESTRICTED)
        .create()

    override val engineName: String = "JEXL"

    override val samples: Map<String, ScriptSample> = engineSamples("jexl")

    override fun prepare(sample: ScriptSample): JexlPreparedScript {
        val script = engine.createScript(sample.content)
        val context = MapContext().apply {
            sample.bindingsFactory().forEach { (key, value) ->
                set(key, value)
            }
        }
        return JexlPreparedScript(context, script)
    }

    override fun execute(prepared: JexlPreparedScript): Any? {
        return prepared.script.execute(prepared.context)
    }

}

internal data class JexlPreparedScript(
    val context: MapContext,
    val script: JexlScript,
)
