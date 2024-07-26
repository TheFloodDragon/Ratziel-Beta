package cn.fd.ratziel.script

import cn.fd.ratziel.common.config.Settings
import cn.fd.ratziel.script.api.ScriptType
import taboolib.common.ClassAppender
import taboolib.common.LifeCycle
import taboolib.common.env.JarRelocation
import taboolib.common.env.RuntimeEnv
import taboolib.common.platform.Awake
import taboolib.library.configuration.ConfigurationSection


/**
 * ScriptManager
 *
 * @author TheFloodDragon
 * @since 2024/7/16 11:30
 */
object ScriptManager {

    /**
     * 默认脚本语言
     */
    var defaultLanguage: ScriptType = ScriptTypes.KETHER
        internal set

    @Awake(LifeCycle.INIT)
    private fun init() {
        // Read config
        val conf = Settings.conf.getConfigurationSection("Script")

        // Default Language
        val defLanguage = conf?.getString("Default")?.let { ScriptTypes.match(it) }
        if (defLanguage != null) defaultLanguage = defLanguage

        // Options
        val optionsConf = conf?.getConfigurationSection("Options")

        // Function - get script options from conf
        fun getOptions(names: Array<out String>): ConfigurationSection? {
            if (optionsConf != null) {
                for (entry in optionsConf.getValues(false)) {
                    if (names.contains(entry.key)) return entry.value as ConfigurationSection
                }
            }
            return null
        }

        // Init
        for (lang in ScriptTypes.entries) {
            val options = getOptions(lang.names)
            lang.enabled = options?.getBoolean("enabled", true) ?: true
            // If the script is enabled
            if (lang.enabled) lang.executor.initEnv()
        }

    }

    /**
     * 加载指定环境环境
     */
    fun loadEnv(value: String, test: String = "", transitive: Boolean = false, relocations: List<JarRelocation> = emptyList()) {
        fun real(url: String) = if (url.startsWith("!")) url.substring(1) else url
        // 检查测试类是否存在
        val testClass = real(test)
        if (testClass.isNotEmpty() && !ClassAppender.isExists(testClass)) return
        // 加载类
        RuntimeEnv.ENV.loadDependency((real(value)), transitive, relocations)
    }

}