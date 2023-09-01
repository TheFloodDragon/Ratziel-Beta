package cn.fd.ratziel.kether.api

/**
 * @author 蛟龙
 * @since 2023/7/29 12:11
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class KetherAction(
    val value: Array<String>,
    val namespace: Array<String> = ["fdu", "kether"],
    val shared: Boolean = true,
)