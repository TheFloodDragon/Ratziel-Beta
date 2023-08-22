package cn.fd.utilities.core.element

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
    private val alias: Set<String>?,

    ) {

    constructor(space: String, name: String) : this(space, name, null)

    /**
     * 获取别名,防止别名中包含元素类型的主名称
     */
    fun getAlias(): Set<String>? {
        return alias?.toMutableSet()?.apply { remove(name) }
    }

    override fun toString(): String {
        return this::class.java.simpleName + '{' +
                "space=" + space + ";" +
                "name=" + name + ';' +
                "alias=" + alias?.toList().toString()
    }

}