@file:Suppress("JavaIoSerializableObjectMustHaveReadResolve")

package cn.fd.ratziel.module.script.lang.kts

import taboolib.common.platform.Ghost
import kotlin.script.experimental.api.*
import kotlin.script.experimental.jvm.JvmDependencyFromClassLoader
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvmhost.jsr223.configureProvidedPropertiesFromJsr223Context
import kotlin.script.experimental.jvmhost.jsr223.importAllBindings
import kotlin.script.experimental.jvmhost.jsr223.jsr223

/**
 * KtsConfigurations
 *
 * @author TheFloodDragon
 * @since 2024/7/15 19:11
 */
@Ghost
object KtsConfigurations {

    /**
     * [kotlin.script.experimental.api.ScriptCompilationConfiguration]
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
    })

    /**
     * [kotlin.script.experimental.api.ScriptEvaluationConfiguration]
     */
    @Ghost
    object Evaluation : ScriptEvaluationConfiguration({
        refineConfigurationBeforeEvaluate(::configureProvidedPropertiesFromJsr223Context)
    })

}