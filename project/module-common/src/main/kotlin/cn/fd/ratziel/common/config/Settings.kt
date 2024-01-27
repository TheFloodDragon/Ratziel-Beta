package cn.fd.ratziel.common.config

import taboolib.common.Inject
import taboolib.common.platform.function.getDataFolder
import taboolib.module.configuration.Config
import taboolib.module.configuration.ConfigNode
import taboolib.module.configuration.Configuration

object Settings {

    private const val FILE = "settings.yml"

    @Config(FILE)
    lateinit var conf: Configuration internal set

    @ConfigNode("Workspaces.listen", FILE)
    var listenFiles = true

    @ConfigNode("Workspaces.paths", FILE)
    var workspacePaths = listOf("${getDataFolder()}/workspace")

    @ConfigNode("Workspaces.filter", FILE)
    var fileFilter = "^(?![#!]).*\\.(?i)(yaml|yml|toml|tml|json|conf)\$"

}