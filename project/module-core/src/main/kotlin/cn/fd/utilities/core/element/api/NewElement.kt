package cn.fd.utilities.core.element.api

/**
 * 用于注册元素处理器
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class NewElement(
    val name: Array<String>,
)