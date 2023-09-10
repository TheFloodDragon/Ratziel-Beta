package cn.fd.ratziel.common.config

import taboolib.common.platform.function.getDataFolder
import taboolib.module.configuration.*
import taboolib.module.lang.Language

object Settings {

    @Config("settings.yml")
    lateinit var conf: Configuration
        private set

    @ConfigNode("Settings.language", "settings.yml")
    var language: String = "zh_CN"
        private set(value) {
            field = value.also { Language.default = value } // 刷新语言
        }

    @ConfigNode("Workspaces.listen", "settings.yml")
    var listenFiles = true

    @ConfigNode("Workspaces.paths", "settings.yml")
    var workspacePaths = listOf("${getDataFolder()}/workspace")

    @ConfigNode("Workspaces.filter", "settings.yml")
    var fileFilter = "^(?![#!]).*\\.(?i)(yaml|yml|toml|tml|json|conf)\$"

}