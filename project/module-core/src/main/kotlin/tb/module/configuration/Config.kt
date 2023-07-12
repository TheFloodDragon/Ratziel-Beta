package tb.module.configuration

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class Config(
    val value: String = "config.yml",
    val target: String = "sameAsValue", //修改部分——添加
    val migrate: Boolean = false,
    val autoReload: Boolean = false
)