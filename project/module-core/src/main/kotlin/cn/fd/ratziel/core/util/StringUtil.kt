package cn.fd.ratziel.core.util

import java.util.function.Consumer

/**
 * 删除当前字符串的所有目标字符
 * 其实就是将 [value] 替换成 ""
 */
fun String.removeAll(value: String, ignoreCase: Boolean = false) = this.replace(value, "", ignoreCase)

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
 * 判断当前字符串是否包含目标字符串, 若包含则执行 [consumer]
 */
fun String.runIfContainsNonEscaped(
    other: String,
    ignoreCase: Boolean = false,
    escapeChar: String = "\\",
    consumer: Consumer<String>,
): Boolean {
    val result =
        // 若有转义字符, 先全部删除
        if (this.contains(escapeChar + other, ignoreCase))
            this.removeAll(escapeChar + other, ignoreCase).contains(other, ignoreCase)
        else this.contains(other, ignoreCase)
    // 若包含目标字符串, 则执行, 提供去掉转义字符的字符串
    if (result) consumer.accept(this.replace(escapeChar + other, other))
    return result
}

/**
 * 字符串自适应 - 尝试将字符串转化为数字
 */
fun String.adapt() = toBooleanStrictOrNull() ?: toBigDecimalOrNull()?.let {
    if (it.scale() > 0) it.toString().toDoubleOrNull() else it.toString().toIntOrNull() ?: toLongOrNull()
} ?: this