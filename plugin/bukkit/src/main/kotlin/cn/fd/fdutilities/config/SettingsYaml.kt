package cn.fd.fdutilities.config

import taboolib.module.configuration.Config
import taboolib.module.configuration.ConfigNode
import taboolib.module.configuration.Configuration

/**
 * 配置文件: settings.yml
 * @author MC~蛟龙
 * @since 2022/6/8 19:22
 */
object SettingsYaml {

    @Config(value = "settings.yml", autoReload = true)
    lateinit var conf: Configuration
        private set


    @ConfigNode(value = "Settings.language", bind = "settings.yml")
    var PLUGIN_LANGUAGE = "en_US"

    @ConfigNode(value = "Settings.Multi-Thread", bind = "settings.yml")
    var MULTI_THREAD = true

}