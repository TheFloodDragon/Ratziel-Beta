package cn.fd.ratziel.module.script.lang.kts

import org.jetbrains.kotlin.cli.common.repl.KotlinJsr223JvmScriptEngineFactoryBase
import org.jetbrains.kotlin.cli.common.repl.ScriptArgsWithTypes
import javax.script.Bindings
import javax.script.ScriptContext
import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.ScriptEvaluationConfiguration
import kotlin.script.experimental.api.with
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

    override fun getScriptEngine() = this.getScriptEngine({}, {})

    fun getScriptEngine(
        compilationBody: ScriptCompilationConfiguration.Builder.() -> Unit = {},
        evaluationBody: ScriptEvaluationConfiguration.Builder.() -> Unit = {},
    ) = KotlinJsr223ScriptEngineImpl(
        this,
        scriptDefinition.compilationConfiguration.with(compilationBody),
        scriptDefinition.evaluationConfiguration.with(evaluationBody),
    ) { ScriptArgsWithTypes(arrayOf(it.getBindings(ScriptContext.ENGINE_SCOPE).orEmpty()), arrayOf(Bindings::class)) }

}