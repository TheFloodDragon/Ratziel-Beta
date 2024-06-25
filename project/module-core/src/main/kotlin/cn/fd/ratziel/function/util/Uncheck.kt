@file:Suppress("NOTHING_TO_INLINE", "UNCHECKED_CAST")

package cn.fd.ratziel.function.util

/**
 * 简便方法 - 用于表示类型已受过检查 (忽略编译器警告)
 * @throws IllegalStateException 当类型转换不成功是抛出
 */
inline fun <R> uncheck(target: Any): R =
    try {
        target as R
    } catch (ex: ClassCastException) {
        throw IllegalStateException("Illegal! Has the type check been performed?", ex)
    }

/**
 * 简便方法 - 用于表示类型已受过检查 (忽略编译器警告)
 * @throws IllegalStateException 当类型转换不成功是抛出
 */
inline fun <R> uncheck(target: Any?): R? {
    return uncheck(target ?: return null)
}