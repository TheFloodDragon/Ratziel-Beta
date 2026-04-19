package cn.fd.ratziel.module.script.benchmark.engine

import cn.fd.ratziel.core.util.randomUuid
import cn.fd.ratziel.module.script.benchmark.BenchmarkCase
import cn.fd.ratziel.module.script.benchmark.ScriptSample
import cn.fd.ratziel.module.script.benchmark.engineSamples
import org.tabooproject.fluxon.Fluxon
import org.tabooproject.fluxon.compiler.CompilationContext
import org.tabooproject.fluxon.interpreter.bytecode.FluxonClassLoader
import org.tabooproject.fluxon.runtime.Environment
import org.tabooproject.fluxon.runtime.FluxonRuntime
import org.tabooproject.fluxon.runtime.RuntimeScriptBase
import taboolib.common.io.digest

internal object FluxonBenchmarkCase : BenchmarkCase<FluxonPreparedScript> {

    override val engineName: String = "Fluxon"

    override val samples: Map<String, ScriptSample> = engineSamples("fluxon", ".fs")

    /**
     * 编译 + 预装配。把 `newInstance()` 和 `Environment` 构造都挪到此处（只算一次），
     * 让 [execute] 真正只测 `runtime.eval(env)` 的耗时。
     */
    override fun prepare(sample: ScriptSample): FluxonPreparedScript {
        val className = sample.path.substringAfterLast('/').replace(Regex("[^A-Za-z0-9_]"), "_") + randomUuid().digest()
        val env = newEnvironment(sample.bindingsFactory())
        val compiled = Fluxon.compile(
            env,
            newCompilationContext(sample.content, sample.path),
            className,
            this::class.java.classLoader,
        )
        val definedClass = compiled.defineClass(FluxonClassLoader())
        val runtime = definedClass.getDeclaredConstructor().newInstance() as RuntimeScriptBase
        return FluxonPreparedScript(runtime, env)
    }

    /** 热执行路径：纯 `eval` 耗时。runtime 实例与环境在 [prepare] 已装配完成，这里不做任何构造。 */
    override fun execute(prepared: FluxonPreparedScript): Any? {
        return prepared.runtime.eval(prepared.environment)
    }

    override fun evaluate(sample: ScriptSample): Any? {
        val interpretEnv = FluxonRuntime.getInstance().newEnvironment()
        val interpretCtx = newCompilationContext(sample.content, sample.path)
        return Fluxon.parse(interpretCtx, interpretEnv).eval(interpretEnv)
    }

    private fun newCompilationContext(source: String, path: String): CompilationContext {
        return CompilationContext(source, path.substringAfterLast('/')).apply {
            setAllowJavaConstruction(true)
            setAllowReflectionAccess(true)
        }
    }

    private fun newEnvironment(bindings: MutableMap<String, Any?>): Environment {
        return FluxonRuntime.getInstance().newEnvironment().apply {
            rootVariables.putAll(bindings)
        }
    }
}

internal data class FluxonPreparedScript(
    val runtime: RuntimeScriptBase,
    val environment: Environment,
)
