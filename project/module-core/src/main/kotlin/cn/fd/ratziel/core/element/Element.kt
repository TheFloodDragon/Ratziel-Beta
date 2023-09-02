package cn.fd.ratziel.core.element

import cn.fd.ratziel.core.serialize.adapt
import kotlinx.serialization.json.JsonElement
import java.io.File
import java.util.function.Supplier

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
    val property: JsonElement?,
    /**
     * 元素地址
     */
    val address: ElementAddress = ElementAddress(id, type, null)
) {

    /**
     * 获取自适应的元素属性
     */
    fun adaptProperty(): Any? {
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

}