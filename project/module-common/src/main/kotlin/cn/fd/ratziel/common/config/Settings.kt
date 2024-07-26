package cn.fd.ratziel.common.config

import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.releaseResourceFile
import taboolib.common.util.asList
import taboolib.module.configuration.Configuration

object Settings {

    /**
     * 配置文件路径
     */
    val file by lazy { releaseResourceFile(source = "settings.yml", target = "settings.yml") }

    /**
     * 配置文件
     */
    lateinit var conf: Configuration
        internal set

    /**
     * 重载配置文件
     */
    fun reloadConfig() {
        conf = Configuration.loadFromFile(file)
    }

    init {
        reloadConfig()
    }

    /**
     * 配置项 - 工作空间路径列表
     */
    val workspacePaths get() = conf["Workspaces.paths"]?.asList() ?: listOf("${getDataFolder()}/workspace")

    /**
     * 配置项 - 工作空间文件过滤器 (正则表达式)
     */
    var fileFilter = conf["Workspaces.filter"]?.toString() ?: "^(?![#!]).*\\.(?i)(yaml|yml|toml|tml|json|conf)\$"

    /**
     * 配置项 - 是否启用文件监听
     */
    val listenFiles get() = conf["Workspaces.listen"]?.toString()?.toBooleanStrictOrNull() ?: true

}