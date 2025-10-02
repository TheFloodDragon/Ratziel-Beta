package cn.fd.ratziel.module.script.lang.kts

import taboolib.common.platform.Ghost
import kotlin.script.experimental.annotations.KotlinScript

/**
 * KtsScript
 *
 * @author TheFloodDragon
 * @since 2024/7/16 00:15
 */
@Ghost
@KotlinScript(
    compilationConfiguration = KtsConfigurations.Compilation::class,
    evaluationConfiguration = KtsConfigurations.Evaluation::class
)
abstract class KtsScript(val vars: Map<String, Any?>)
