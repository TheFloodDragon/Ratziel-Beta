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
    private val alias: Array<String>,

    ) {

    constructor(space: String, name: String) : this(space, name, emptyArray())

    /**
     * 获取别名
     * 不包含元素类型主名称
     */
    fun getAlias(): Set<String> {
        return alias.toMutableSet().apply { remove(name) }
    }

    /**
     * 获取所有名称
     * 包含元素类型主名称
     */
    fun getAllNames(): Set<String> {
        return alias.toMutableSet().apply { add(name) }
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