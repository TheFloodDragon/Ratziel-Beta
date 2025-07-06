package cn.fd.ratziel.module.script.impl

import taboolib.library.configuration.ConfigurationSection

/**
 * ScriptBootstrap
 *
 * @author TheFloodDragon
 * @since 2025/6/6 18:39
 */
interface ScriptBootstrap {

    /**
     * 初始化函数
     */
    fun initialize(settings: ConfigurationSection)

}