package cn.fd.ratziel.module.script.internal

import taboolib.library.configuration.ConfigurationSection

/**
 * Initializable
 *
 * @author TheFloodDragon
 * @since 2025/2/8 12:47
 */
internal interface Initializable {

    /**
     * 初始化函数
     */
    fun initialize(settings: ConfigurationSection)

}