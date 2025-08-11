package cn.fd.ratziel.module.script.lang

import cn.fd.ratziel.core.functional.AttachedContext
import cn.fd.ratziel.core.util.JarUtil
import cn.fd.ratziel.module.script.ScriptManager
import cn.fd.ratziel.module.script.ScriptType
import cn.fd.ratziel.module.script.api.ScriptExecutor
import cn.fd.ratziel.module.script.impl.ScriptBootstrap
import cn.fd.ratziel.module.script.lang.js.GraalJsScriptExecutor
import cn.fd.ratziel.module.script.lang.js.NashornScriptExecutor
import taboolib.library.configuration.ConfigurationSection
import java.util.concurrent.CopyOnWriteArrayList

/**
 * JavaScriptLang
 *
 * @author TheFloodDragon
 * @since 2025/7/6 15:28
 */
object JavaScriptLang : ScriptType, ScriptBootstrap {

    override val name = "JavaScript"

    override val alias = arrayOf("Js")

    override val extensions = arrayOf("js", "javascript")

    override val executor get() = if (::selectedExecutor.isInitialized) selectedExecutor else super.executor

    /**
     * 全局脚本列表
     */
    val globalScripts: MutableList<Pair<String, AttachedContext>> = CopyOnWriteArrayList()

    /**
     * 选择的脚本执行器 (Nashorn 或 GraalJS)
     */
    private lateinit var selectedExecutor: ScriptExecutor

    override fun initialize(settings: ConfigurationSection) {

        // 读取引擎
        val selected = settings.getString("engine")
        // 设置全局使用的脚本执行器
        selectedExecutor = when (selected?.lowercase()) {
            "nashorn" -> {
                ScriptManager.loadDependencies("nashorn") // 加载 Nashorn JavaScript 依赖
                NashornScriptExecutor
            }

            "graaljs" -> {
                ScriptManager.loadDependencies("graaljs") // 加载 GraalVM JavaScript 依赖
                GraalJsScriptExecutor
            }

            else -> throw IllegalArgumentException("Unknown engine '$selected' selected.")
        }

        // 读取全局脚本
        globalScripts.addAll(JarUtil.getEntries {
            it.name.startsWith("script-default/") && it.name.endsWith("extensions.js")
        }.map { it.reader().readText() to AttachedContext.newContext() })
    }

}