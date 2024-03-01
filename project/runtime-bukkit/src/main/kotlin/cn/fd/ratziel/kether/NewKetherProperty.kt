package cn.fd.ratziel.kether

import kotlin.reflect.KClass

/**
 * KetherProperty
 *
 * @author TheFloodDragon
 * @since 2023/9/8 22:30
 */
@Deprecated("Need a new design")
annotation class NewKetherProperty(
    /**
     * 属性标识符
     */
    val id: String,
    /**
     * 绑定类
     */
    val bind: KClass<*>,
    /**
     * 是否分享
     */
    val shared: Boolean = true,
)

