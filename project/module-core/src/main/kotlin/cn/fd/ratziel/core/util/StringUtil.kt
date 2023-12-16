package cn.fd.ratziel.core.util


/**
 * 获取某个字符的索引列表
 */
@JvmOverloads
fun String.allIndexOf(string: String, startIndex: Int = 0, ignoreCase: Boolean = false, action: (Int) -> Unit) {
    var index: Int = startIndex
    while (index > -1) {
        index = indexOf(string, index + 1, ignoreCase).also { action(it) }
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
    // 被截取后的字符串
    var sub = this@replaceNonEscaped
    // 转义符之后的部分
    var afterEscape = String()
    // 所有转义字符串索引
    this@replaceNonEscaped.allIndexOf(escapeChar) { ei ->
        afterEscape = sub.substring(ei + escapeChar.length)
        // 添加在转义字符之前(替换后)的
        append(sub.substring(0, ei).replace(oldValue, newValue, ignoreCase))
        // 若转义字符后面就是要替换的字符,添加要替换的字符串
        if (afterEscape.startsWith(oldValue, ignoreCase)) {
            append(oldValue); sub = afterEscape.drop(oldValue.length)
        } else {
            append(escapeChar); sub = afterEscape
        }
    }
    append(afterEscape.replace(oldValue, newValue, ignoreCase)) // 尾处理
}
