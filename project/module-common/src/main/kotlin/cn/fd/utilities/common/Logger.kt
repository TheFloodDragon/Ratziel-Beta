package cn.fd.utilities.common

import cn.fd.utilities.common.Logger.process

fun debug(info: Any? = Any(), level: LogLevel = LogLevel.Medium, auto: Boolean = true) {
    if (Logger.debug && level == Logger.level)
        if (auto) process(info)
        else println(info)
}

fun log(info: Any? = Any(), level: LogLevel = LogLevel.Medium, auto: Boolean = true) {
    if (level == Logger.level)
        if (auto) process(info)
        else println(info)
}

object Logger {
    //是否启用Debug模式
    val debug: Boolean = true

    val level: LogLevel = LogLevel.Medium

    fun process(info: Any?) {
        when (info) {
            is Iterable<*> -> info.forEach { println(it) }
            is Map<*, *> -> info.forEach { println(it.key.toString() + "  |  " + it.value.toString()) }
            else -> println(info)
        }
    }
}

enum class LogLevel {
    Lowest,
    Low,
    Lower,
    Medium,
    Higher,
    High,
    Highest
}