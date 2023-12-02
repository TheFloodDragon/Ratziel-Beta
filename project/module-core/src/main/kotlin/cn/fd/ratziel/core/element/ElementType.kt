package cn.fd.ratziel.core.element

typealias EType = ElementType

/**
 * 元素类型
 */
open class ElementType(

    /**
     * 元素类型所在空间
     */
    open val space: String,

    /**
     * 元素类型主名称
     */
    open val name: String,

    ) {

    /**
     * 元素类型别名
     */
    var alias: Array<String> = emptyArray()
        protected set

    /**
     * 元素类型名称包括别名
     */
    val appellations
        get() = alias.plus(name)

    /**
     * 带别名的构造函数
     */
    constructor(space: String, name: String, alias: Array<String>) : this(space, name) {
        this.alias = alias.toMutableSet().apply { remove(name) }.toTypedArray() // 规范别名
    }

    override fun toString() =
        this::class.java.simpleName + '{' +
                "space=" + space + ";" +
                "name=" + name + ';' +
                "alias=" + alias.toList().toString() + '}'

    override fun equals(other: Any?) = other is ElementType && this.space == other.space && this.name == other.name

    override fun hashCode() = space.hashCode() + name.hashCode()

}