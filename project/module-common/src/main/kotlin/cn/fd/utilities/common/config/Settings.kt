package cn.fd.utilities.common.config

import taboolib.module.configuration.ConfigNode
import taboolib.module.configuration.Configuration
import tb.module.configuration.Config

object Settings {

    @Config
    lateinit var conf: Configuration
        private set

    @ConfigNode(value = "Settings.language")
    var LANGUAGE = "zh_CN"

//    @ConfigNode(value = "Settings.Multi-Thread")
//    var MULTI_THREAD = true

    @ConfigNode(value = "Workspaces.paths")
    var WORKSPACES_PATHS = listOf("plugins/FDUtilities/workspace")

}