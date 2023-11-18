package cn.fd.ratziel.common.element.registry

import taboolib.common.LifeCycle

/**
 * 元素配置
 *
 * @author TheFloodDragon
 * @since 2023/10/4 13:23
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ElementConfig(
    /**
     * 在指定生命周期时元素处理
     */
    val lifeCycle: LifeCycle = LifeCycle.LOAD,
    /**
     * 是否同步处理
     */
    val sync: Boolean = false,
)