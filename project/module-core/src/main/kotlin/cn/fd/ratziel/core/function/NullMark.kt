package cn.fd.ratziel.core.function

/**
 * NullMark - 空标记
 *
 * @author TheFloodDragon
 * @since 2023/11/4 12:27
 */
object NullMark {

    /**
     * 字符串表达形式
     */
    const val value = "null"

    override fun toString(): String = value

}

fun Any.isMarkedNull() = this is NullMark