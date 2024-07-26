package cn.fd.ratziel.script

import cn.fd.ratziel.common.config.Settings
import cn.fd.ratziel.script.api.ScriptType
import taboolib.common.LifeCycle
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

    var defaultScriptLanguage: ScriptType = ScriptTypes.KETHER
        internal set

    @Awake(LifeCycle.LOAD)
    private fun init() {
        // Read config
        val conf = Settings.conf.getConfigurationSection("Script")

        // Default Language
        val defLanguage = conf?.getString("Default")?.let { ScriptTypes.match(it) }
        if (defLanguage != null) defaultScriptLanguage = defLanguage

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
            if (lang.enabled) {
                // LoadEnv
                RuntimeEnv.ENV.loadDependency(lang.executor::class.java)
            }
        }
    }

}