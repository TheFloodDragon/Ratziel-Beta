package cn.fd.utilities.core.element

import taboolib.module.configuration.Configuration


/**
 * 元素(唯一的)
 */
class Element(
    /**
     * 元素标识符
     */
    val id: String,
    /**
     * 元素类型
     */
    val type: ElementType,
    /**
     * 元素属性
     */
    private var conf: Configuration? //TODO Use Kotlin Serialization
) {

    constructor(id: String, type: ElementType) : this(id, type, null)

    fun getConfig(): Configuration? {
        return conf
    }

}