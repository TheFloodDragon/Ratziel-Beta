package cn.fd.ratziel.module.script.performance

import org.graalvm.polyglot.Context
import org.graalvm.polyglot.Engine
import org.graalvm.polyglot.Source

internal object GraalJsBenchmarkCase : BenchmarkCase<GraalPreparedScript> {

    private val sharedEngine: Engine = Engine.newBuilder("js")
        .allowExperimentalOptions(true)
        .option("js.ecmascript-version", "latest")
        .option("js.nashorn-compat", "true")
        .build()

    override val engineName: String = "GraalJS"

    override val samples: Map<String, ScriptSample> = engineSamples("javascript")

    override fun prepare(sample: ScriptSample): GraalPreparedScript {
        val context = Context.newBuilder("js")
            .allowAllAccess(true)
            .engine(sharedEngine)
            .build()
        val source = Source.newBuilder("js", sample.content, sample.path)
            .cached(true)
            .build()
        return GraalPreparedScript(context, source)
    }

    override fun execute(prepared: GraalPreparedScript): Any? {
        return prepared.context.eval(prepared.source).`as`(Any::class.java)
    }

    override fun dispose(prepared: GraalPreparedScript) {
        prepared.close()
    }

}

internal data class GraalPreparedScript(
    val context: Context,
    val source: Source,
) : AutoCloseable {
    override fun close() {
        context.close()
    }
}
