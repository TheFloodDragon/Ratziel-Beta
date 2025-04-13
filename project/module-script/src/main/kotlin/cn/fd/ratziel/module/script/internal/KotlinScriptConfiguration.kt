package cn.fd.ratziel.module.script.internal

import taboolib.common.platform.Ghost
import kotlin.script.experimental.api.*
import kotlin.script.experimental.jvm.JvmDependencyFromClassLoader
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvmhost.jsr223.configureProvidedPropertiesFromJsr223Context
import kotlin.script.experimental.jvmhost.jsr223.importAllBindings
import kotlin.script.experimental.jvmhost.jsr223.jsr223

/**
 * KotlinScriptConfiguration
 *
 * @author TheFloodDragon
 * @since 2024/7/15 19:11
 */
@Ghost
object KotlinScriptConfiguration {

    /**
     * [ScriptCompilationConfiguration]
     */
    @Ghost
    object Compilation : ScriptCompilationConfiguration({
        refineConfiguration {
            beforeCompiling(::configureProvidedPropertiesFromJsr223Context)
        }
        jvm {
            dependencies(JvmDependencyFromClassLoader { this::class.java.classLoader })
        }
        jsr223 {
            importAllBindings(true)
        }
        ide {
            acceptedLocations(ScriptAcceptedLocation.Everywhere)
        }
    }) {
        @Suppress("unused")
        private fun readResolve(): Any = Compilation
    }

    /**
     * [ScriptEvaluationConfiguration]
     */
    @Ghost
    object Evaluation : ScriptEvaluationConfiguration({
        refineConfigurationBeforeEvaluate(::configureProvidedPropertiesFromJsr223Context)
    }) {
        @Suppress("unused")
        private fun readResolve(): Any = Evaluation
    }

}