package cn.fd.ratziel.kether

/**
 * @author 蛟龙
 * @since 2023/7/29 12:11
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Deprecated("Need a new design")
annotation class NewKetherAction(
    val value: Array<String>,
    val namespace: Array<String> = ["ratziel", "kether"],
    val shared: Boolean = true,
)