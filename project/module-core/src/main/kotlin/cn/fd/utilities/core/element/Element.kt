package cn.fd.utilities.core.element

import taboolib.module.configuration.Configuration


/**
 * 元素(唯一的)
 */
class Element(
    /**
     * 元素的命名空间
     */
    val space: String,
    /**
     * 元素名称
     */
    val name: Array<String>,
    /**
     * 元素属性
     */
    private var conf: Configuration? //TODO Use Kotlin Serialization
) {

    constructor(space: String, name: Array<String>) : this(space, name, null)

    fun getConfig(): Configuration? {
        return conf
    }

}