package cn.fd.ratziel.module.compat.hook

import taboolib.common.LifeCycle

/**
 * HookInject
 *
 * @author TheFloodDragon
 * @since 2024/2/17 12:05
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class HookInject(
    val lifeCycle: LifeCycle = LifeCycle.ENABLE
)