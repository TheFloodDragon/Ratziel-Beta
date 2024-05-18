package cn.fd.ratziel.core.util

import taboolib.common.io.digest
import java.util.*

/**
 * 加密 (默认SHA-256)
 */
fun String.digest() = digest(DEFAULT_ALGORITHM)

const val DEFAULT_ALGORITHM = "SHA-256"

fun randomUUID() = UUID.randomUUID().toString().replace("-", "").lowercase()

const val ESCAPE_CHAR = "\\"

/**
 * 删除当前字符串的所有目标字符
 * 其实就是将 [value] 替换成 ""
 */
fun String.removeAll(value: String, ignoreCase: Boolean = false) = this.replace(value, "", ignoreCase)

/**
 * 获取单个非转义字符串的索引位置
 * @return 如果为空则代表找到了转义后的目标字符串
 */
@JvmOverloads
fun String.indexOfNonEscaped(
    target: String,
    startIndex: Int = 0,
    ignoreCase: Boolean = false,
    escapeChar: String = ESCAPE_CHAR,
) = this.indexOf(target, startIndex, ignoreCase).takeUnless { this.substring(0, it).endsWith(escapeChar, ignoreCase) }

/**
 * 获取所有非转义字符串的索引位置
 */
@JvmOverloads
fun String.allIndexOfNonEscaped(
    target: String,
    startIndex: Int = 0,
    ignoreCase: Boolean = false,
    escapeChar: String = ESCAPE_CHAR,
    action: (Int) -> Unit,
) {
    var index = indexOfNonEscaped(target, startIndex, ignoreCase, escapeChar)
    while (index != null && index > -1) {
        action(index); index = indexOfNonEscaped(target, index + 1, ignoreCase, escapeChar)
    }
}

@JvmOverloads
fun String.allIndexOfNonEscaped(
    target: String,
    startIndex: Int = 0,
    ignoreCase: Boolean = false,
    escapeChar: String = ESCAPE_CHAR,
) = buildList { allIndexOfNonEscaped(target, startIndex, ignoreCase, escapeChar) { add(it) } }.toTypedArray()


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
    escapeChar: String = ESCAPE_CHAR,
): String = buildString {
    // 索引记录
    var lastIndex = 0
    // 匹配字符串
    allIndexOf(oldValue, startIndex, ignoreCase) { index ->
        // 从上次找到的位置到当前找到的位置之前的字符串
        val segment = this@replaceNonEscaped.substring(lastIndex, index)
        // 检查转义字符串
        if (this@replaceNonEscaped.startsWith(escapeChar, index - escapeChar.length))
            append(segment.dropLast(escapeChar.length)).append(oldValue)
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
    escapeChar: String = ESCAPE_CHAR,
): List<String> = this.split(*delimiters, ignoreCase = ignoreCase, limit = limit)
    .map { s -> s.takeUnless { it.endsWith(escapeChar) } ?: s.dropLast(escapeChar.length) }

/**
 * 字符串自适应 - 尝试将字符串转化为数字
 */
fun String.adapt() = toBooleanStrictOrNull() ?: toBigDecimalOrNull()?.let {
    if (it.scale() > 0) it.toString().toDoubleOrNull() else it.toString().toIntOrNull() ?: toLongOrNull()
} ?: this