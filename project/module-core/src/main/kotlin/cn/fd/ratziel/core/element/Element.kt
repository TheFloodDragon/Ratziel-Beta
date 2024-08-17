package cn.fd.ratziel.core.element

import kotlinx.serialization.json.JsonElement
import java.io.File

class Element(
    /**
     * 元素标识符
     */
    val identifier: ElementIdentifier,
    /**
     * 元素属性
     */
    val property: JsonElement,
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
     * 元素文件
     */
    val file get() = identifier.file

    constructor(
        id: String,
        type: ElementType,
        file: File?,
        property: JsonElement,
    ) : this(ElementIdentifier(id, type, file), property)

    override fun toString() = "Element(identifier=$identifier, property=$property)"

    override fun equals(other: Any?) =
        other is Element && this.identifier == other.identifier && this.property == other.property

    override fun hashCode() = 31 * property.hashCode() + identifier.hashCode()

}