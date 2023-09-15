package cn.fd.ratziel.core.element

import cn.fd.ratziel.core.serialize.adapt
import kotlinx.serialization.json.JsonElement
import java.io.File

open class Element(
    /**
     * 元素标识符
     */
    open val id: String,
    /**
     * 元素类型
     */
    open val type: ElementType,
    /**
     * 元素属性
     */
    open val property: JsonElement?,
    /**
     * 元素地址
     */
    open val address: ElementAddress = ElementAddress(id, type, null),
) {

    /**
     * 获取自适应的元素属性
     */
    open fun adaptProperty(): Any? {
        return property?.adapt()
    }

    constructor(
        id: String,
        file: File?,
        type: ElementType,
        property: JsonElement?,
    ) : this(id, type, property, ElementAddress(id, type, file))

    constructor(
        address: ElementAddress,
        property: JsonElement?,
    ) : this(address.id, address.file, address.type, property)

    override fun equals(other: Any?): Boolean {
        return if (other is Element) {
            this.address == other.address
        } else super.equals(other)
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + (property?.hashCode() ?: 0)
        result = 31 * result + address.hashCode()
        return result
    }

}