package cn.fd.fdutilities.module.outdated

import cn.fd.fdutilities.config.ConfigYaml
import taboolib.module.configuration.Configuration

@Deprecated("过时")
object PlaceholderAPIExtension : Extension("PlaceholderAPI") {

    override val resourcePath: String = "module/Extensions/PlaceholderAPI.yml"

    override var path: String =
        ConfigYaml.conf.getString("Modules.PlaceholderAPI.file") ?: "Extensions\\PlaceholderAPI.yml"

    override lateinit var conf: Configuration


    override fun init() {
        isEnabled = pluginEnabled && ConfigYaml.conf.getBoolean("Modules.PlaceholderAPI.enabled", true)
    }

    override fun onReload() {}

}