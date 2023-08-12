package cn.fd.utilities.core.api.kether

/**
 * @author 蛟龙
 * @since 2023/7/29 12:11
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class KetherAction(
    val name: Array<String>,
    val namespace: String = "fdutilities"
)