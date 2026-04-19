package cn.fd.ratziel.module.script.benchmark.engine

import cn.fd.ratziel.module.script.benchmark.BenchmarkCase
import cn.fd.ratziel.module.script.benchmark.ScriptSample
import cn.fd.ratziel.module.script.benchmark.engineSamples
import org.apache.commons.jexl3.JexlBuilder
import org.apache.commons.jexl3.JexlContext
import org.apache.commons.jexl3.JexlFeatures
import org.apache.commons.jexl3.JexlScript
import org.apache.commons.jexl3.introspection.JexlPermissions

internal object JexlBenchmarkCase : BenchmarkCase<JexlPreparedScript> {

    private val engine = JexlBuilder()
        .cache(16)
        .strict(true)
        .features(JexlFeatures.createAll())
        .permissions(JexlPermissions.UNRESTRICTED)
        .create()

    override val engineName: String = "Jexl"

    override val samples: Map<String, ScriptSample> = engineSamples("jexl", ".jexl")

    override fun compile(sample: ScriptSample): JexlPreparedScript {
        val script = engine.createScript(sample.content)
        val context = BenchmarkJexlContext(sample.bindingsFactory())
        return JexlPreparedScript(context, script)
    }

    override fun runCompiled(compiled: JexlPreparedScript): Any? {
        return compiled.script.execute(compiled.context)
    }

    override fun interpret(sample: ScriptSample): Any? {
        return engine.createScript(sample.content)
            .execute(BenchmarkJexlContext(sample.bindingsFactory()))
    }
}

internal data class JexlPreparedScript(
    val context: BenchmarkJexlContext,
    val script: JexlScript,
)

internal class BenchmarkJexlContext(
    private val bindings: MutableMap<String, Any?> = linkedMapOf(),
) : JexlContext {

    override fun get(name: String): Any? = bindings[name]

    override fun has(name: String): Boolean = bindings.containsKey(name)

    override fun set(name: String, value: Any?) {
        bindings[name] = value
    }
}
