package cn.fd.ratziel.module.script.lang.js

import cn.fd.ratziel.module.script.ScriptManager
import cn.fd.ratziel.module.script.api.ScriptExecutor
import cn.fd.ratziel.module.script.api.ScriptPreference
import cn.fd.ratziel.module.script.api.ScriptType
import cn.fd.ratziel.module.script.impl.ScriptBootstrap
import taboolib.library.configuration.ConfigurationSection

/**
 * JavaScriptLang
 *
 * @author TheFloodDragon
 * @since 2025/7/6 15:28
 */
object JavaScriptLang : ScriptType, ScriptBootstrap {

    override val name = "JavaScript"
    override val languageId = "js"
    override val alias = arrayOf("Js")
    override val extensions = arrayOf("js", "javascript")
    override val preference = ScriptPreference.COMPILATION_PREFERRED

    override val executor: ScriptExecutor get() = if (::selectedExecutor.isInitialized) selectedExecutor else super.executor

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
    }

}