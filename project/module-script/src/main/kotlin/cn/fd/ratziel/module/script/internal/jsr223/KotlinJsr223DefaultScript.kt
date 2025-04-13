@file:Suppress("unused")

package cn.fd.ratziel.module.script.internal.jsr223

import cn.fd.ratziel.module.script.internal.KotlinScriptConfiguration
import taboolib.common.platform.Ghost
import kotlin.script.experimental.annotations.KotlinScript

/**
 * KotlinJsr223DefaultScript
 *
 * @author TheFloodDragon
 * @since 2024/7/16 00:15
 */
@Ghost
@KotlinScript(
    compilationConfiguration = KotlinScriptConfiguration.Compilation::class,
    evaluationConfiguration = KotlinScriptConfiguration.Evaluation::class
)
abstract class KotlinJsr223DefaultScript(val vars: Map<String, Any?>)