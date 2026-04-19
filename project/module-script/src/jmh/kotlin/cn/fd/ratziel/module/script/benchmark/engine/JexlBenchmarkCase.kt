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

    /** 热执行/编译阶段共用的 engine。保留默认缓存大小（16），因为缓存正是生产使用方式。 */
    private val hotEngine = JexlBuilder()
        .cache(16)
        .strict(true)
        .features(JexlFeatures.createAll())
        .permissions(JexlPermissions.UNRESTRICTED)
        .create()

    override val engineName: String = "Jexl"

    override val samples: Map<String, ScriptSample> = engineSamples("jexl", ".jexl")

    override fun prepare(sample: ScriptSample): JexlPreparedScript {
        val script = hotEngine.createScript(sample.content)
        val context = BenchmarkJexlContext(sample.bindingsFactory())
        return JexlPreparedScript(context, script)
    }

    override fun execute(prepared: JexlPreparedScript): Any? {
        return prepared.script.execute(prepared.context)
    }

    /**
     * 冷启动路径：每次创建**无缓存**的一次性 engine，保证测得真冷启动成本。
     * 若复用 [hotEngine]（cache=16），第 2 次起的 createScript 会直接命中缓存，
     * 与其他引擎（Fluxon 纯解释、Nashorn AST eval、GraalJS 新 Context）语义不对等。
     */
    override fun evaluate(sample: ScriptSample): Any? {
        val coldEngine = JexlBuilder()
            .cache(0)
            .strict(true)
            .features(JexlFeatures.createAll())
            .permissions(JexlPermissions.UNRESTRICTED)
            .create()
        return coldEngine.createScript(sample.content)
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
