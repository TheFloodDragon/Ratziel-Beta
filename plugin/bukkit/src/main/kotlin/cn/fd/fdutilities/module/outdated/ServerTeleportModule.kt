package cn.fd.fdutilities.module.outdated

import cn.fd.fdutilities.config.ConfigYaml
import taboolib.module.configuration.Configuration


@Deprecated("过时")
object ServerTeleportModule : Module() {

    override val resourcePath: String = "module/ServerTeleport.yml"

    override var path: String = ConfigYaml.conf.getString("Modules.ServerTeleport.file") ?: "ServerTeleport.yml"

    override lateinit var conf: Configuration

    override fun init() {
        isEnabled = ConfigYaml.conf.getBoolean("Modules.ServerTeleport.enabled", true)
    }

    override fun onReload() {}

}