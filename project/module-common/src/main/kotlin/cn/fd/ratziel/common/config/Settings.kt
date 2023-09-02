package cn.fd.ratziel.common.config

import taboolib.common.io.newFolder
import taboolib.common.platform.function.getDataFolder
import taboolib.module.configuration.ConfigNode
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Config

object Settings {

    val defaultWorkspace = newFolder("${getDataFolder()}/workspace", false)

    @Config
    lateinit var conf: Configuration
        private set

    @ConfigNode(value = "Settings.language")
    var Language = "zh_CN"

//    @ConfigNode(value = "Settings.Multi-Thread")
//    var MULTI_THREAD = true

    @ConfigNode(value = "Workspaces.paths")
    var WorkspacePaths = listOf(defaultWorkspace.path)

    @ConfigNode(value = "Workspaces.filter")
    var fileFilter = "^(?![#!]).*\\.(?i)(yaml|yml|toml|tml|json|conf)\$"

}