package cn.fd.ratziel.common.config

import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.releaseResourceFile
import taboolib.module.configuration.Config
import taboolib.module.configuration.ConfigNode
import taboolib.module.configuration.Configuration

object Settings {

    /**
     * 配置文件路径
     */
    val file by lazy { releaseResourceFile("settings.yml") }

    @Config("settings.yml")
    private lateinit var _conf: Configuration

    /**
     * 配置文件
     */
    val conf: Configuration
        get() {
            if (!::_conf.isInitialized) _conf = Configuration.loadFromFile(file)
            return _conf
        }

    /**
     * 配置项 - 工作空间路径列表
     */
    @ConfigNode("Workspaces.paths", "settings.yml")
    var workspacePaths = listOf("${getDataFolder()}/workspace")
        private set

    /**
     * 配置项 - 工作空间文件过滤器 (正则表达式)
     */
    @ConfigNode("Workspaces.filter", "settings.yml")
    var fileFilter = "^(?![#!]).*\\.(?i)(yaml|yml|toml|tml|json|conf)$"
        private set

    /**
     * 配置项 - 是否启用文件监听
     */
    @ConfigNode("Workspaces.listen", "settings.yml")
    var listenFiles = true
        private set

    /**
     * 配置项 - 是否在消息组件序列化的过程中取消斜体
     */
    @ConfigNode("Message.non-italic-by-default", "settings.yml")
    var nonItalic = true
        private set

}
