package cn.fd.fdutilities.config

import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration

/**
 * 配置文件: config.yml
 * @author MC~蛟龙
 * @since 2022/5/28 22:14
 */
@PlatformSide([Platform.BUKKIT])
object ConfigYaml {

    @Config(value = "config.yml" ,autoReload = true)
    lateinit var conf: Configuration
        private set

}