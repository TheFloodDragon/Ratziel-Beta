package cn.fd.ratziel.core.element

import kotlinx.serialization.json.JsonElement
import java.io.File

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
    private var property: JsonElement? //TODO Use Kotlin Serialization
) {

    /**
     * 元素地址
     */
    var address: ElementAddress? = null

    /**
     * 获取元素属性
     */
    fun getProperty(): JsonElement? {
        return property
    }

    /**
     * 其它构造器
     */
    constructor(
        /**
         * 元素标识符
         */
        id: String,
        /**
         * 元素所在文件路径(若为空则不是文件地址)
         */
        file: File?,
        /**
         * 元素类型所在空间
         */
        type: ElementType,
        /**
         * 元素属性
         */
        property: JsonElement?,
    ) : this(id, type, property) {
        // 元素地址赋值
        this.address = ElementAddress(id, this.type, file)
    }

    constructor(
        /**
         * 元素地址
         */
        address: ElementAddress,
        /**
         * 元素属性
         */
        property: JsonElement?,
    ) : this(address.id, address.file, address.type, property)

    constructor(
        /**
         * 元素标识符
         */
        id: String,
        /**
         * 元素所在文件路径(若为空则不是文件地址)
         */
        file: File?,
        /**
         * 元素类型所在空间
         */
        space: String,
        /**
         * 元素类型主名称
         */
        name: String,
        /**
         * 元素类型别名
         */
        alias: Set<String>,
        /**
         * 元素属性
         */
        property: JsonElement?,
    ) : this(id, file, ElementType(space, name, alias), property)

    constructor(
        /**
         * 元素标识符
         */
        id: String,
        /**
         * 元素类型所在空间
         */
        space: String,
        /**
         * 元素类型主名称
         */
        name: String,
        /**
         * 元素类型别名
         */
        alias: Set<String>,
        /**
         * 元素属性
         */
        property: JsonElement?
    ) : this(id, ElementType(space, name, alias), property)

}