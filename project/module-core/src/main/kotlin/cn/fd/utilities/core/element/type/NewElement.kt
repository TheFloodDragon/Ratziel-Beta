package cn.fd.utilities.core.element.type

/**
 * 用于注册元素处理器
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class NewElement(
    /**
     * 元素类型名称
     */
    val name: String,
    /**
     * 元素类型别名
     */
    val alias: Array<String> = emptyArray(),
    /**
     * 命名空间
     */
    val space: String = "ef"
)
