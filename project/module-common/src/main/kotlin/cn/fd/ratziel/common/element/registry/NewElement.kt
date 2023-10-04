package cn.fd.ratziel.common.element.registry

import cn.fd.ratziel.core.element.service.ElementRegistry

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
    val alias: Array<String> = [],
    /**
     * 命名空间
     */
    val space: String = "ez",
    /**
     * 如果使用此注解的类是元素处理器
     * 则该项代表着元素处理器的优先级
     */
    val priority: Byte = ElementRegistry.DEFAULT_PRIORITY,
)