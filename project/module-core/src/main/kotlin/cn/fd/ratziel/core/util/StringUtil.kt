package cn.fd.ratziel.core.util

/**
 * 获取某个字符的索引列表
 */
@JvmOverloads
fun String.allIndexOf(string: String, startIndex: Int = 0, ignoreCase: Boolean = false, action: (Int) -> Unit) {
    var index: Int = startIndex
    while (index > -1 && index < string.length - 1) {
        index = indexOf(string, index, ignoreCase).also { if (it > -1) action(it) }
    }
}

@JvmOverloads
fun String.allIndexOf(string: String, startIndex: Int = 0, ignoreCase: Boolean = false): Array<Int> =
    buildList { allIndexOf(string, startIndex, ignoreCase) { add(it) } }.toTypedArray()

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
): String = buildString {
    // 索引记录
    var lastIndex = 0
    // 匹配字符串
    allIndexOf(oldValue, ignoreCase = ignoreCase) { index ->
        // 从上次找到的位置到当前找到的位置之前的字符串
        val segment = this.substring(lastIndex, index)
        // 检查转义字符串
        if (index >= escapeChar.length && this.startsWith(escapeChar, index - escapeChar.length))
            append(segment.dropLast(escapeChar.length)).append(oldValue)
        else
            append(segment).append(newValue)
        // 更新索引
        lastIndex = index + oldValue.length
    }
    append(this.substring(lastIndex)) // 尾处理
}