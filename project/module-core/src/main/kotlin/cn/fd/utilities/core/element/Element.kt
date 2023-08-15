package cn.fd.utilities.core.element

import taboolib.module.configuration.Configuration


/**
 * 元素(唯一的)
 */
//TODO Use Kotlin Serialization
class Element(
    private val conf: Configuration
) {

    fun getConfig(): Configuration {
        return conf
    }

}