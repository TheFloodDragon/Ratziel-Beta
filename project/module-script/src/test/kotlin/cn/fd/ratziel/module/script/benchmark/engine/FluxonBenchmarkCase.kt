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

    override fun prepare(sample: ScriptSample): FluxonPreparedScript {
        val className = sample.path.substringAfterLast('/').replace(Regex("[^A-Za-z0-9_]"), "_") + randomUuid().digest()
        val compiled = Fluxon.compile(
            newEnvironment(sample.bindingsFactory()),
            newCompilationContext(sample.content, sample.path),
            className,
            this::class.java.classLoader,
        )
        val definedClass = compiled.defineClass(FluxonClassLoader())
        return FluxonPreparedScript(definedClass, sample.bindingsFactory)
    }

    override fun execute(prepared: FluxonPreparedScript): Any? {
        val runtime = prepared.definedClass.getDeclaredConstructor().newInstance() as RuntimeScriptBase
        return runtime.eval(newEnvironment(prepared.bindingsFactory()))
    }

    override fun evaluate(sample: ScriptSample): Any? {
        return Fluxon.eval(sample.content, newEnvironment(sample.bindingsFactory()))
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
    val definedClass: Class<*>,
    val bindingsFactory: () -> MutableMap<String, Any?>,
)
