package cn.fd.ratziel.core.util

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * 随机 UUID
 */
@Suppress("NOTHING_TO_INLINE")
@OptIn(ExperimentalUuidApi::class)
inline fun randomUuid() = Uuid.random().toHexString()


/**
 * 获取当前字符串中目标字符的所有索引并执行相应的操作
 */
private fun String.allIndexOf(target: String, startIndex: Int = 0, ignoreCase: Boolean = false, action: (Int) -> Unit) {
    var index = indexOf(target, startIndex, ignoreCase)
    while (index > -1) {
        action(index); index = indexOf(target, index + 1, ignoreCase)
    }
}

/**
 * 替换非转义字符
 * @see String.replace
 */
@JvmOverloads
fun String.replaceNonEscaped(
    oldValue: String,
    newValue: String,
    ignoreCase: Boolean = false,
    startIndex: Int = 0,
): String = buildString {
    // 索引记录
    var lastIndex = 0
    // 匹配字符串
    allIndexOf(oldValue, startIndex, ignoreCase) { index ->
        // 从上次找到的位置到当前找到的位置之前的字符串
        val segment = this@replaceNonEscaped.substring(lastIndex, index)
        // 检查转义字符串
        if (index > 0 && this@replaceNonEscaped[index - 1] == '\\')
            append(segment.dropLast(1)).append(oldValue)
        else append(segment).append(newValue)
        // 更新索引
        lastIndex = index + oldValue.length
    }
    append(this@replaceNonEscaped.substring(lastIndex)) // 尾处理
}

/**
 * 分割非转义字符
 * @see String.split
 */
@JvmOverloads
fun String.splitNonEscaped(
    vararg delimiters: String,
    ignoreCase: Boolean = false,
    limit: Int = 0,
): List<String> = this.split(*delimiters, ignoreCase = ignoreCase, limit = limit)
    .map { s -> s.takeUnless { it.endsWith('\\') } ?: s.dropLast(1) }

/**
 * 字符串自适应 - 尝试将字符串转化为数字
 */
fun String.adapt() = toBooleanStrictOrNull() ?: toBigDecimalOrNull()?.let {
    if (it.scale() > 0) it.toString().toDoubleOrNull() else it.toString().toIntOrNull() ?: toLongOrNull()
} ?: this