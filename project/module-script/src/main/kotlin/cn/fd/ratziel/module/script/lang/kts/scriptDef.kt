@file:Suppress("JavaIoSerializableObjectMustHaveReadResolve")

package cn.fd.ratziel.module.script.lang.kts

import taboolib.common.platform.function.getDataFolder
import java.io.File
import java.nio.ByteBuffer
import java.security.MessageDigest
import kotlin.script.experimental.annotations.KotlinScript
import kotlin.script.experimental.api.*
import kotlin.script.experimental.host.FileBasedScriptSource
import kotlin.script.experimental.host.FileScriptSource
import kotlin.script.experimental.host.ScriptingHostConfiguration
import kotlin.script.experimental.jvm.JvmDependencyFromClassLoader
import kotlin.script.experimental.jvm.compilationCache
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvmhost.CompiledScriptJarsCache
import kotlin.script.experimental.jvmhost.jsr223.configureProvidedPropertiesFromJsr223Context
import kotlin.script.experimental.jvmhost.jsr223.importAllBindings
import kotlin.script.experimental.jvmhost.jsr223.jsr223
import kotlin.script.experimental.util.filterByAnnotationType

/**
 * KtsScript
 *
 * @author TheFloodDragon
 * @since 2024/7/16 00:15
 */
@KotlinScript(
    compilationConfiguration = KtsCompilationConfiguration::class,
    evaluationConfiguration = KtsEvaluationConfiguration::class,
    hostConfiguration = KtsHostConfiguration::class
)
abstract class KtsScript(val vars: Map<String, Any?>)


/**
 * Compilation Configuration
 */
object KtsCompilationConfiguration : ScriptCompilationConfiguration({
    defaultImports(KtsScript::class, Import::class)
    refineConfiguration {
        beforeCompiling(::configureProvidedPropertiesFromJsr223Context)
        onAnnotations(Import::class, handler = MainKtsConfigurator())
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
 * Evaluation Configuration
 */
object KtsEvaluationConfiguration : ScriptEvaluationConfiguration({
    // TODO 看注释貌似是把脚本当库用
//    scriptsInstancesSharing(true)
    refineConfigurationBeforeEvaluate(::configureProvidedPropertiesFromJsr223Context)
})

/**
 * Host Configuration
 */
object KtsHostConfiguration : ScriptingHostConfiguration({
    jvm {
        // TODO another configuration way
        val cacheBaseDir = getDataFolder().resolve(".cache/")
            ?.also { it.mkdirs() }
        if (cacheBaseDir != null)
            compilationCache(
                CompiledScriptJarsCache { script, scriptCompilationConfiguration ->
                    File(cacheBaseDir, compiledScriptUniqueName(script, scriptCompilationConfiguration) + ".jar")
                }
            )
    }
})


class MainKtsConfigurator : RefineScriptCompilationConfigurationHandler {

    override operator fun invoke(context: ScriptConfigurationRefinementContext): ResultWithDiagnostics<ScriptCompilationConfiguration> =
        processAnnotations(context)

    fun processAnnotations(context: ScriptConfigurationRefinementContext): ResultWithDiagnostics<ScriptCompilationConfiguration> {
        val diagnostics = arrayListOf<ScriptDiagnostic>()

        val annotations = context.collectedData?.get(ScriptCollectedData.collectedAnnotations)?.takeIf { it.isNotEmpty() }
            ?: return context.compilationConfiguration.asSuccess()

        val scriptBaseDir = (context.script as? FileBasedScriptSource)?.file?.parentFile
        val importedSources = linkedMapOf<String, Pair<File, String>>()
        var hasImportErrors = false
        annotations.filterByAnnotationType<Import>().forEach { scriptAnnotation ->
            scriptAnnotation.annotation.paths.forEach { sourceName ->
                val file = (scriptBaseDir?.resolve(sourceName) ?: File(sourceName)).normalize()
                val keyPath = file.absolutePath
                val prevImport = importedSources.put(keyPath, file to sourceName)
                if (prevImport != null) {
                    diagnostics.add(
                        ScriptDiagnostic(
                            ScriptDiagnostic.unspecifiedError, "Duplicate imports: \"${prevImport.second}\" and \"$sourceName\"",
                            sourcePath = context.script.locationId, location = scriptAnnotation.location?.locationInText
                        )
                    )
                    hasImportErrors = true
                }
            }
        }
        if (hasImportErrors) return ResultWithDiagnostics.Failure(diagnostics)

        return ScriptCompilationConfiguration(context.compilationConfiguration) {
            if (importedSources.isNotEmpty()) importScripts.append(importedSources.values.map { FileScriptSource(it.first) })
        }.asSuccess()
    }
}

/**
 * 创建编译后的脚本的唯一名称的
 */
private fun compiledScriptUniqueName(script: SourceCode, compilationConfiguration: ScriptCompilationConfiguration): String {
    val digestWrapper = MessageDigest.getInstance("SHA-256")

    fun addToDigest(chunk: String) = with(digestWrapper) {
        val chunkBytes = chunk.toByteArray()
        update(chunkBytes.size.toByteArray())
        update(chunkBytes)
    }

    addToDigest(script.text)
    compilationConfiguration.notTransientData.entries
        .sortedBy { it.key.name }
        .forEach {
            addToDigest(it.key.name)
            addToDigest(it.value.toString())
        }
    return digestWrapper.digest().toHexString()
}

private fun Int.toByteArray() = ByteBuffer.allocate(Int.SIZE_BYTES).also { it.putInt(this) }.array()
