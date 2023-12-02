package cn.fd.ratziel.core.element

typealias ElementAddress = ElementIdentifier

/**
 * ElementIdentifier - 元素唯一标识符
 *
 * @author TheFloodDragon
 * @since 2023/8/21 10:49
 */
open class ElementIdentifier(
    /**
     * 元素名称
     */
    open val name: String,
    /**
     * 元素类型
     */
    open val type: ElementType,
    /**
     * 元素路径
     */
    open val path: String?,
) {

    override fun toString() =
        this::class.java.simpleName + '{' +
                "name=" + name + ";" +
                "type=" + type + ";" +
                "path=" + path + '}'

    override fun equals(other: Any?) =
        other is ElementIdentifier && this.name == other.name && this.type == other.type && this.path == other.path

    override fun hashCode() = name.hashCode() + type.hashCode() + path.hashCode()

}