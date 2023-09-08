package cn.fd.ratziel.kether.loader

/**
 * @author 蛟龙
 * @since 2023/7/29 12:11
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class KetherAction(
    val value: Array<String>,
    val namespace: Array<String> = ["ratziel", "kether"],
    val shared: Boolean = true,
)