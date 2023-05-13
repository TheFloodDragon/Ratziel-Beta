package cn.fd.utilities.config

import taboolib.module.configuration.Config
import taboolib.module.configuration.ConfigNode
import taboolib.module.configuration.Configuration

/**
 * 配置文件: config.yml
 * @author MC~蛟龙
 * @since 2023/5/13 15:28
 */
object ConfigYaml {

    @Config(value = "config.yml", autoReload = true)
    lateinit var conf: Configuration
        private set


    @ConfigNode(value = "Settings.language")
    var LANGUAGE = "zh_CN"

    @ConfigNode(value = "Settings.Multi-Thread")
    var MULTI_THREAD = true

    @ConfigNode(value = "Workspaces.paths")
    var WORKSPACES_PATHS = listOf("plugins/FDUtilities/workspace")

}