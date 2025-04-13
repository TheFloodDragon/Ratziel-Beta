package cn.fd.ratziel.core.element

/**
 * 元素类型
 */
class ElementType(
    /**
     * 元素类型所在空间
     */
    val space: String,
    /**
     * 元素类型主名称
     */
    val name: String,
    /**
     * 元素类型别名
     */
    val alias: Array<String> = emptyArray()
) {

    override fun toString() = "ElementType(space=$space, name=$name, alias=${alias.joinToString(", ", "[", "]")})"

    override fun equals(other: Any?) = other is ElementType && this.space == other.space && this.name == other.name

    override fun hashCode() = 31 * (31 * space.hashCode() + name.hashCode()) + alias.contentHashCode()

}