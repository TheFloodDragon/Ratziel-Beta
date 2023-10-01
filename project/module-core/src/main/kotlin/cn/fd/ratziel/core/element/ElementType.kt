package cn.fd.ratziel.core.element

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

    val appellations
        get() = alias.toMutableList().apply { add(name) }

    /**
     * 带别名的构造函数
     */
    constructor(space: String, name: String, alias: Array<String>) : this(space, name) {
        this.alias = alias.toMutableSet().apply { remove(name) }.toTypedArray() // 规范别名
    }

    override fun toString(): String {
        return this::class.java.simpleName + '{' +
                "space=" + space + ";" +
                "name=" + name + ';' +
                "alias=" + alias.toString() + '}'
    }

    override fun equals(other: Any?): Boolean {
        return if (other is ElementType) {
            this.space == other.space && this.name == other.name
        } else super.equals(other)
    }

    override fun hashCode(): Int {
        var result = space.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + alias.hashCode()
        return result
    }

}