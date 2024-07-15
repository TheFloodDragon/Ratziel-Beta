package cn.fd.ratziel.script.kts

import kotlin.script.experimental.api.*
import kotlin.script.experimental.jvm.JvmDependencyFromClassLoader
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvm.jvmTarget
import kotlin.script.experimental.jvmhost.jsr223.configureProvidedPropertiesFromJsr223Context
import kotlin.script.experimental.jvmhost.jsr223.importAllBindings
import kotlin.script.experimental.jvmhost.jsr223.jsr223

/**
 * KotlinScriptConfiguration
 *
 * @author TheFloodDragon
 * @since 2024/7/15 19:11
 */
object KotlinScriptConfiguration {

    /**
     * [ScriptCompilationConfiguration]
     */
    val compilation = ScriptCompilationConfiguration {
        refineConfiguration {
            beforeCompiling(::configureProvidedPropertiesFromJsr223Context)
        }
        jvm {
            dependencies(JvmDependencyFromClassLoader { this::class.java.classLoader })
            jvmTarget(System.getProperty("java.specification.version") ?: "1.8")
        }
        jsr223 {
            importAllBindings(true)
        }
        ide {
            acceptedLocations(ScriptAcceptedLocation.Everywhere)
        }
    }

    /**
     * [ScriptEvaluationConfiguration]
     */
    val evaluation = ScriptEvaluationConfiguration {
        refineConfigurationBeforeEvaluate(::configureProvidedPropertiesFromJsr223Context)
    }

}