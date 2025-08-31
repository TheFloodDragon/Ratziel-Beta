package cn.fd.ratziel.module.script.element

import cn.fd.ratziel.common.Workspace
import cn.fd.ratziel.common.element.ElementLoader
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.util.resolveOrAbsolute
import cn.fd.ratziel.module.script.ScriptType
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import taboolib.common.platform.function.console
import taboolib.common.platform.function.warning
import taboolib.module.configuration.Configuration
import taboolib.module.lang.sendLang
import java.io.File
import kotlin.time.TimeSource

/**
 * ScriptElementLoader
 *
 * @author TheFloodDragon
 * @since 2025/8/11 14:54
 */
object ScriptElementLoader : ElementLoader {

    override fun allocate(workspace: Workspace, files: Collection<File>): Collection<File> {
        val timeMark = TimeSource.Monotonic.markNow()
        // 脚本文件分组
        val filesGroup = files.groupBy { matchType(it) }
        // 寻找脚本文件配置
        val descriptions = filesGroup[null]?.mapNotNull { loadDescription(it) }.orEmpty()
        val descriptionsIndexed = descriptions.associateBy { it.scriptFile }

        // 脚本文件和描述的对应
        val scriptsGroup = filesGroup.filterKeys { it != null }

        // 开启协程任务编译脚本
        runBlocking {
            val scriptsTasks = ArrayList<Deferred<Pair<File, ScriptFile>>>()
            for ((type, files) in scriptsGroup) {
                for (scriptFile in files) {
                    val desc = descriptionsIndexed[scriptFile]
                        ?: ScriptFile.Description(scriptFile, null, type!!)
                    // ScriptFile 的创建会编译脚本
                    scriptsTasks += async {
                        scriptFile to ScriptFile(desc)
                    }
                }
            }
            val scripts = scriptsTasks.awaitAll()
            // 清空脚本文件列表
            ScriptElementManager.scriptFiles.clear()
            // 加入新的脚本文件
            ScriptElementManager.scriptFiles.putAll(scripts)
            // 提醒
            console().sendLang("ScriptFile-Load", scripts.size, timeMark.elapsedNow().inWholeMilliseconds)
        }

        // 返回占据的文件
        return scriptsGroup.flatMap { it.value }.plus(descriptions.mapNotNull { it.descFile })
    }

    override fun load(workspace: Workspace, file: File) = Result.success(emptyList<Element>())

    fun loadDescription(descFile: File): ScriptFile.Description? {
        // 获取配置文件类型
        val confType = Configuration.getTypeFromExtensionOrNull(descFile.extension) ?: return null
        // 读取配置文件
        val conf = Configuration.loadFromFile(descFile, confType)

        // 读取基本属性
        val scriptFile = conf.getString("file")
            ?.let { descFile.resolveOrAbsolute(it) } ?: return null
        if (!scriptFile.exists()) {
            warning("ScriptFile $scriptFile does not exist!")
            return null
        }
        val language = matchType(scriptFile)
        if (language == null) {
            warning("No matching script type found for $scriptFile!")
            return null
        }

        return ScriptFile.Description(scriptFile, descFile, language)
    }

    /**
     * 匹配脚本文件类型
     */
    fun matchType(file: File) = ScriptType.registry.find { lang ->
        lang.extensions.any { it.equals(file.extension, true) }
    }

}