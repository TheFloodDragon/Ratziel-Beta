package cn.fd.ratziel.core.function

/**
 * NullMark - 空标记
 *
 * @author TheFloodDragon
 * @since 2023/11/4 12:27
 */
@Deprecated("暂时没用")
object NullMark {

    /**
     * 字符串表达形式
     */
    const val value = "null"

    override fun toString(): String = value

}

@Deprecated("暂时没用", ReplaceWith("CNM"))
fun Any.isMarkedNull() = this is NullMark