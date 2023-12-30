package cn.fd.ratziel.core.util

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
    escapeChar: String = "\\",
    startIndex: Int = 0,
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
 * 字符串自适应 - 尝试将字符串转化为数字
 */
fun String.adapt() = toBooleanStrictOrNull() ?: toBigDecimalOrNull()?.let {
    if (it.scale() > 0) it.toString().toDoubleOrNull() else it.toString().toIntOrNull() ?: toLongOrNull()
} ?: this