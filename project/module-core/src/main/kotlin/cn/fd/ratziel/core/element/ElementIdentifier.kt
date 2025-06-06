package cn.fd.ratziel.core.element

import cn.fd.ratziel.core.Identifier
import java.io.File

/**
 * ElementIdentifier - 元素唯一标识符
 *
 * @author TheFloodDragon
 * @since 2023/8/21 10:49
 */
class ElementIdentifier(
    /**
     * 元素名称
     */
    val name: String,
    /**
     * 元素类型
     */
    val type: ElementType,
    /**
     * 元素文件
     */
    val file: File?,
) : Identifier {

    override val content get() = this.name

    override fun toString() = "ElementIdentifier(name=$name, type=$type, path=${file?.path})"

    override fun equals(other: Any?) = other is ElementIdentifier && this.name == other.name && this.type == other.type

    override fun hashCode() = 31 * (31 * name.hashCode() + type.hashCode())

}