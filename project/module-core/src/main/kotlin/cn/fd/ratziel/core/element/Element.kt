package cn.fd.ratziel.core.element

import kotlinx.serialization.json.JsonElement
import java.io.File

open class Element(
    /**
     * 元素标识符
     */
    open val identifier: ElementIdentifier,
    /**
     * 元素属性
     */
    open val property: JsonElement,
) {

    /**
     * 元素名称
     */
    val name get() = identifier.name

    /**
     * 元素类型
     */
    val type get() = identifier.type

    /**
     * 元素路径
     */
    val path get() = identifier.path

    constructor(
        id: String,
        type: ElementType,
        path: String?,
        property: JsonElement,
    ) : this(ElementIdentifier(id, type, path), property)

    constructor(
        id: String,
        type: ElementType,
        file: File?,
        property: JsonElement,
    ) : this(ElementIdentifier(id, type, file?.path), property)

    override fun toString() =
        this::class.java.simpleName + '{' +
                "identifier=" + identifier + ";" +
                "property=" + property + '}'

    /**
     * 相似 - 即元素标识符相同
     */
    fun isSimilar(other: Any?) = other is Element && this.identifier == other.identifier

    override fun equals(other: Any?) =
        other is Element && this.identifier == other.identifier && this.property == other.property

    override fun hashCode() = identifier.hashCode() + property.hashCode()

}