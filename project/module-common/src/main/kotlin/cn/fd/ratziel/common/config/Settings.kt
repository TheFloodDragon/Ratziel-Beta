package cn.fd.ratziel.common.config

import taboolib.common.platform.function.getDataFolder
import taboolib.module.configuration.*
import taboolib.module.lang.Language

object Settings {

    @Config(value = "settings.yml")
    lateinit var conf: Configuration
        private set

    @ConfigNode(value = "Settings.language")
    var language: String = "zh_CN"
        private set(value) {
            field = value.also { Language.default = value } // 刷新语言
        }

    @ConfigNode(value = "Workspaces.listen")
    var listenFiles = true

    @ConfigNode(value = "Workspaces.paths")
    var workspacePaths = listOf("${getDataFolder()}/workspace")

    @ConfigNode(value = "Workspaces.filter")
    var fileFilter = "^(?![#!]).*\\.(?i)(yaml|yml|toml|tml|json|conf)\$"

}