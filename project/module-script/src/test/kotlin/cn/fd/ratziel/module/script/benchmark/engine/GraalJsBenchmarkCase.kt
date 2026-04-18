package cn.fd.ratziel.module.script.benchmark.engine

import cn.fd.ratziel.module.script.benchmark.BenchmarkCase
import cn.fd.ratziel.module.script.benchmark.ScriptSample
import cn.fd.ratziel.module.script.benchmark.engineSamples
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

    override val samples: Map<String, ScriptSample> = engineSamples("javascript", ".js")

    override fun prepare(sample: ScriptSample): GraalPreparedScript {
        val context = Context.newBuilder("js")
            .allowAllAccess(true)
            .engine(sharedEngine)
            .build()
        val bindings = context.getBindings("js")
        sample.bindingsFactory().forEach { (key, value) ->
            bindings.putMember(key, value)
        }
        val source = Source.newBuilder("js", sample.content, sample.path)
            .cached(true)
            .build()
        return GraalPreparedScript(context, source)
    }

    override fun execute(prepared: GraalPreparedScript): Any? {
        return prepared.context.eval(prepared.source).`as`(Any::class.java)
    }

    override fun evaluate(sample: ScriptSample): Any? {
        val context = Context.newBuilder("js")
            .allowAllAccess(true)
            .engine(sharedEngine)
            .build()
        try {
            val bindings = context.getBindings("js")
            sample.bindingsFactory().forEach { (key, value) ->
                bindings.putMember(key, value)
            }
            val source = Source.newBuilder("js", sample.content, sample.path)
                .cached(false)
                .build()
            return context.eval(source).`as`(Any::class.java)
        } finally {
            context.close()
        }
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
