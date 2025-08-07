package cn.fd.ratziel.common.element.registry

import cn.fd.ratziel.core.element.ElementHandler
import taboolib.common.LifeCycle
import kotlin.reflect.KClass

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
     * 需要/依赖 的元素处理器
     */
    val requires: Array<KClass<out ElementHandler>> = [],
    /**
     * 并行处理
     */
    val parallel: Boolean = true,
)