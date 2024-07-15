package cn.fd.ratziel.script.internal.jsr223

import cn.fd.ratziel.script.kts.KotlinScriptConfiguration
import javax.script.Bindings
import kotlin.script.experimental.annotations.KotlinScript
import kotlin.script.templates.standard.ScriptTemplateWithBindings

/**
 * KotlinJsr223DefaultScript
 *
 * @author TheFloodDragon
 * @since 2024/7/16 00:15
 */
@KotlinScript(
    compilationConfiguration = KotlinScriptConfiguration.Compilation::class,
    evaluationConfiguration = KotlinScriptConfiguration.Evaluation::class
)
abstract class KotlinJsr223DefaultScript(bindings: Bindings) : ScriptTemplateWithBindings(bindings)