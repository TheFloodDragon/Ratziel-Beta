package cn.fd.ratziel.module.script.element

import cn.fd.ratziel.common.element.registry.NewElement
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.element.ElementHandler
import cn.fd.ratziel.module.script.api.ScriptType
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import taboolib.common.platform.function.console
import taboolib.common.platform.function.warning
import taboolib.module.lang.sendLang
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.TimeSource

/**
 * ScriptElementHandler
 *
 * @author TheFloodDragon
 * @since 2025/9/6 22:49
 */
@NewElement("script")
object ScriptElementHandler : ElementHandler.Updatable {

    /**
     * 脚本描述表
     */
    @JvmField
    val descriptions: MutableMap<File, ScriptDescription> = ConcurrentHashMap()

    /**
     * 脚本文件表
     */
    @JvmField
    val scriptFiles: MutableMap<File, ScriptFile> = ConcurrentHashMap()

    override suspend fun handle(elements: Collection<Element>) = coroutineScope {
        // 时间开始标记
        val timeMark = TimeSource.Monotonic.markNow()

        // 脚本文件分组
        val typedGroup = elements.map { it.file }.groupBy { ScriptElementLoader.matchType(it.extension) }
        // 脚本文件组
        val scriptsGroup = typedGroup.filterKeys { it != null }
        // 脚本描述组
        val descriptionsGroup = typedGroup[null].orEmpty()

        // 读取所有脚本文件
        val descMap = descriptionsGroup.map { descFile ->
            async { ScriptDescription.loadFromFile(descFile)?.let { descFile to it } }
        }.awaitAll().filterNotNull().toMap()

        // 清空并设置描述表
        descriptions.clear(); descriptions.putAll(descMap)

        // 创建脚本文件
        val scriptsTasks = ArrayList<Deferred<Pair<File, ScriptFile>?>>()
        for ((type, files) in scriptsGroup) {
            for (file in files) {
                // 开启协程任务编译脚本
                scriptsTasks += async { createScriptFile(file, type!!) }
            }
        }
        val scripts = scriptsTasks.awaitAll().filterNotNull()

        // 清空并设置脚本文件表
        scriptFiles.clear(); scriptFiles.putAll(scripts)

        // 提醒
        console().sendLang("ScriptFile-Load", scripts.size, timeMark.elapsedNow().inWholeMilliseconds)
    }

    override suspend fun update(element: Element) {
        val file = element.file
        val language = ScriptElementLoader.matchType(file.extension)
        // 脚本文件
        if (language != null) {
            val scriptsFile = createScriptFile(file, language) ?: return
            scriptFiles[scriptsFile.first] = scriptsFile.second
        } else {
            // 描述文件
            val desc = ScriptDescription.loadFromFile(file)
            if (desc != null) descriptions[file] = desc
        }
    }

    /**
     * 创建脚本文件
     */
    @JvmStatic
    fun createScriptFile(file: File, language: ScriptType) = try {
        val desc = descriptions.values.find { it.files.contains(file) } ?: ScriptDescription(listOf(file))
        // 构建脚本文件
        val scriptFile = ScriptFile(file, desc, language)
        file to scriptFile
    } catch (ex: Exception) {
        warning("Failed to compile script file $file!", ex.stackTraceToString()); null
    }

}