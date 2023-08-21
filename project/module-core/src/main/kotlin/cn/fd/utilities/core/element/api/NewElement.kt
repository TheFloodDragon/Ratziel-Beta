package cn.fd.utilities.core.element.api

/**
 * 用于注册元素处理器
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class NewElement(
    /**
     * 元素名称
     */
    val alias: Array<String>,
    /**
     * 标识符,用于区分同名元素
     */
    val space: String = "ef",
)