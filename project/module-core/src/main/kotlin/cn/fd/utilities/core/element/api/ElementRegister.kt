package cn.fd.utilities.core.element.api

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ElementRegister(
    val name: Array<String>,
)