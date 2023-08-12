package cn.fd.utilities.common.config

import taboolib.common.platform.function.getDataFolder
import taboolib.module.configuration.ConfigNode
import taboolib.module.configuration.Configuration
import tb.module.configuration.Config
import java.io.File

object Settings {

    val defaultWorkspace by lazy { File(getDataFolder(), "workspace") }

    @Config
    lateinit var conf: Configuration
        private set

    @ConfigNode(value = "Settings.language")
    var LANGUAGE = "zh_CN"

//    @ConfigNode(value = "Settings.Multi-Thread")
//    var MULTI_THREAD = true

    @ConfigNode(value = "Workspaces.paths")
    var WORKSPACES_PATHS = listOf(defaultWorkspace.path)

}