package cn.fd.ratziel.common.annotation

import taboolib.common.LifeCycle

/**
 * 用于标记某函数的执行需要在特特定插件生命周期
 *
 * @author TheFloodDragon
 * @since 2023/10/4 13:23
 */
@Target(AnnotationTarget.CLASS,AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class OnLifeCycle(
    val cycle : LifeCycle
)