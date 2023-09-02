package cn.fd.ratziel.core.util

import taboolib.common.platform.event.ProxyEvent
import java.util.function.Function

/**
 * 触发一个事件并运行后续操作
 */
fun <T : ProxyEvent, R> T.callThenRun(function: Function<T, R>): R? {
    return this.also { it.call() }
        .takeIf { !it.isCancelled }
        ?.let { function.apply(this) }
}