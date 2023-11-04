package cn.fd.ratziel.common

import taboolib.common.io.isDevelopmentMode
import taboolib.common.platform.function.info

fun debug(info: Any? = "", level: LogLevel = LogLevel.Medium) = Logger.debug(info, level)

fun log(info: Any? = "", level: LogLevel = LogLevel.Medium) = Logger.log(info, level)

object Logger {

    /**
     * 是否启用Debug模式
     */
    var debug: Boolean = false
        get() = field || isDevelopmentMode

    /**
     * 日志等级
     */
    var level: LogLevel = LogLevel.Highest

    /**
     * 发送日志
     */
    fun log(info: Any?, level: LogLevel = LogLevel.Medium) {
        if (level.priority >= Logger.level.priority) info(info.toString())
    }

    /**
     * 发送Debug日志
     */
    fun debug(info: Any?, level: LogLevel = LogLevel.Medium) {
        if (debug) log(info, level)
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