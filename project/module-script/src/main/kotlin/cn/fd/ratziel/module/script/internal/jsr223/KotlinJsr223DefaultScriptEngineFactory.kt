package cn.fd.ratziel.module.script.internal.jsr223

import org.jetbrains.kotlin.cli.common.repl.KotlinJsr223JvmScriptEngineFactoryBase
import org.jetbrains.kotlin.cli.common.repl.ScriptArgsWithTypes
import javax.script.Bindings
import javax.script.ScriptContext
import javax.script.ScriptEngine
import kotlin.script.experimental.jvmhost.createJvmScriptDefinitionFromTemplate
import kotlin.script.experimental.jvmhost.jsr223.KotlinJsr223ScriptEngineImpl

/**
 * KotlinJsr223DefaultScriptEngineFactory
 *
 * @author TheFloodDragon
 * @since 2024/7/15 19:02
 */
@Suppress("unused")
class KotlinJsr223DefaultScriptEngineFactory : KotlinJsr223JvmScriptEngineFactoryBase() {

    val scriptDefinition = createJvmScriptDefinitionFromTemplate<KotlinJsr223DefaultScript>()

    override fun getScriptEngine(): ScriptEngine =
        KotlinJsr223ScriptEngineImpl(
            this,
            scriptDefinition.compilationConfiguration,
            scriptDefinition.evaluationConfiguration,
        ) { ScriptArgsWithTypes(arrayOf(it.getBindings(ScriptContext.ENGINE_SCOPE).orEmpty()), arrayOf(Bindings::class)) }

}