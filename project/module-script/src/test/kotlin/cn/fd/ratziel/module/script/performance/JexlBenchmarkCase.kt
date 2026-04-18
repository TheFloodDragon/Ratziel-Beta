package cn.fd.ratziel.module.script.performance

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

    override val dialect: ScriptDialect = ScriptDialect.Jexl

    override val samples: Map<String, ScriptSample> = engineSamples(dialect)

    override fun prepare(sample: ScriptSample): JexlPreparedScript {
        val script = engine.createScript(sample.content)
        return JexlPreparedScript(MapContext(), script)
    }

    override fun execute(prepared: JexlPreparedScript): Any? {
        return prepared.script.execute(prepared.context)
    }

}

internal data class JexlPreparedScript(
    val context: MapContext,
    val script: JexlScript,
)
