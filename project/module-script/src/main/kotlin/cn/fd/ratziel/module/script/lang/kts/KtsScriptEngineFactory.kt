package cn.fd.ratziel.module.script.lang.kts

import org.jetbrains.kotlin.cli.common.repl.KotlinJsr223JvmScriptEngineFactoryBase
import org.jetbrains.kotlin.cli.common.repl.ScriptArgsWithTypes
import java.io.File
import javax.script.Bindings
import javax.script.ScriptContext
import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.ScriptEvaluationConfiguration
import kotlin.script.experimental.api.with
import kotlin.script.experimental.jvm.JvmScriptCompilationConfigurationBuilder
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvm.updateClasspath
import kotlin.script.experimental.jvm.util.scriptCompilationClasspathFromContext
import kotlin.script.experimental.jvmhost.createJvmScriptDefinitionFromTemplate
import kotlin.script.experimental.jvmhost.jsr223.KotlinJsr223ScriptEngineImpl

/**
 * KtsScriptEngineFactory
 *
 * @author TheFloodDragon
 * @since 2024/7/15 19:02
 */
@Suppress("unused")
object KtsScriptEngineFactory : KotlinJsr223JvmScriptEngineFactoryBase() {

    private val scriptDefinition = createJvmScriptDefinitionFromTemplate<KtsScript>()

    private var lastClassLoader: ClassLoader? = null
    private var lastClassPath: List<File>? = null

    @Synchronized
    private fun JvmScriptCompilationConfigurationBuilder.dependenciesFromCurrentContext() {
        val currentClassLoader = Thread.currentThread().contextClassLoader
        val classPath = if (lastClassLoader == null || lastClassLoader != currentClassLoader) {
            scriptCompilationClasspathFromContext(
                classLoader = currentClassLoader,
                wholeClasspath = true,
                unpackJarCollections = true
            ).also {
                lastClassLoader = currentClassLoader
                lastClassPath = it
            }
        } else lastClassPath!!
        updateClasspath(classPath)
    }

    override fun getScriptEngine() = this.getScriptEngine({}, {})

    fun getScriptEngine(
        compilationBody: ScriptCompilationConfiguration.Builder.() -> Unit = {},
        evaluationBody: ScriptEvaluationConfiguration.Builder.() -> Unit = {},
    ) = KotlinJsr223ScriptEngineImpl(
        this,
        scriptDefinition.compilationConfiguration.with {
            jvm {
                dependenciesFromCurrentContext()
            }
        }.with(compilationBody),
        scriptDefinition.evaluationConfiguration.with(evaluationBody),
    ) { ScriptArgsWithTypes(arrayOf(it.getBindings(ScriptContext.ENGINE_SCOPE).orEmpty()), arrayOf(Bindings::class)) }

}