package cn.fd.ratziel.common

import cn.fd.ratziel.common.Logger.process

fun debug(info: Any? = Any(), level: LogLevel = LogLevel.Medium, auto: Boolean = true) {
    if (Logger.debug) log(info, level, auto)
}

fun log(info: Any? = Any(), level: LogLevel = LogLevel.Medium, auto: Boolean = true) {
    if (level.priority >= Logger.level.priority)
        if (auto) process(info)
        else println(info)
}

object Logger {

    /**
     * 是否启用Debug模式
     */
    val debug: Boolean = true

    /**
     * 日志等级
     */
    val level: LogLevel = LogLevel.Highest

    /**
     * 日志智能处理
     */
    fun process(info: Any?) {
        println("Process: $info")
        when (info) {
            is Iterable<*> -> info.forEach { println(it) }
            is Map<*, *> -> info.forEach { println(it.key.toString() + "  |  " + it.value.toString()) }
            else -> println(info)
        }
    }
}

/**
 * 日志等级
 */
enum class LogLevel(val priority: Byte) {
    Lowest(-3),
    Low(-2),
    Lower(-1),
    Medium(0),
    Higher(1),
    High(2),
    Highest(3);
}