package cn.fd.ratziel.common.element.registry

import taboolib.common.LifeCycle

/**
 * 元素处理配置
 *
 * @author TheFloodDragon
 * @since 2023/10/4 13:23
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ElementConfig(
    /**
     * 指定生命周期时处理
     */
    val lifeCycle: LifeCycle = LifeCycle.LOAD,
    /**
     * 异步处理 TODO remove this
     */
    val async: Boolean = false,
)