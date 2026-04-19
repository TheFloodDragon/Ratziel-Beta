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

    override fun compile(sample: ScriptSample): FluxonPreparedScript {
        val className = sample.path.substringAfterLast('/').replace(Regex("[^A-Za-z0-9_]"), "_") + randomUuid().digest()
        val compiled = Fluxon.compile(
            newEnvironment(sample.bindingsFactory()),
            newCompilationContext(sample.content, sample.path),
            className,
            this::class.java.classLoader,
        )
        val definedClass = compiled.defineClass(FluxonClassLoader())
        val runtime = definedClass.getDeclaredConstructor().newInstance() as RuntimeScriptBase
        return FluxonPreparedScript(runtime, sample.bindingsFactory)
    }

    override fun runCompiled(compiled: FluxonPreparedScript): Any? {
        return compiled.runtime.eval(newEnvironment(compiled.bindingsFactory()))
    }

    override fun interpret(sample: ScriptSample): Any? {
        val interpretEnv = newEnvironment(sample.bindingsFactory())
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
    val bindingsFactory: () -> MutableMap<String, Any?>,
)
