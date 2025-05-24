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
 * 检查字符串是否包含非转义的字符串
 */
@JvmOverloads
fun String.containsNonEscaped(other: String, ignoreCase: Boolean = false): Boolean {
    return allIndexOfNonEscaped(other, ignoreCase = ignoreCase).isNotEmpty()
}

/**
 * 获取单个非转义字符串的索引位置
 * @return 如果为空则代表找到了转义后的目标字符串
 */
@JvmOverloads
fun String.indexOfNonEscaped(
    target: String,
    startIndex: Int = 0,
    ignoreCase: Boolean = false,
) = this.indexOf(target, startIndex, ignoreCase)
    .takeUnless { it > 0 && this[it - 1].equals('\\', ignoreCase) } ?: -1

/**
 * 获取所有非转义字符串的索引位置
 */
@JvmOverloads
fun String.allIndexOfNonEscaped(
    target: String,
    startIndex: Int = 0,
    ignoreCase: Boolean = false,
    action: (Int) -> Unit,
) {
    var index = indexOfNonEscaped(target, startIndex, ignoreCase)
    while (index >= 0) {
        action(index); index = indexOfNonEscaped(target, index + 1, ignoreCase)
    }
}

@JvmOverloads
fun String.allIndexOfNonEscaped(
    target: String,
    startIndex: Int = 0,
    ignoreCase: Boolean = false,
) = buildList { allIndexOfNonEscaped(target, startIndex, ignoreCase) { add(it) } }.toTypedArray()


/**
 * 获取当前字符串中目标字符的所有索引并执行相应的操作
 */
@JvmOverloads
fun String.allIndexOf(target: String, startIndex: Int = 0, ignoreCase: Boolean = false, action: (Int) -> Unit) {
    var index = indexOf(target, startIndex, ignoreCase)
    while (index > -1) {
        action(index); index = indexOf(target, index + 1, ignoreCase)
    }
}

/**
 * 获取当前字符串中目标字符的所有索引的集合
 */
@JvmOverloads
fun String.allIndexOf(target: String, startIndex: Int = 0, ignoreCase: Boolean = false): Array<Int> =
    buildList { allIndexOf(target, startIndex, ignoreCase) { add(it) } }.toTypedArray()

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