package cn.fd.ratziel.common.function

import cn.fd.ratziel.core.function.futureRunAsync
import taboolib.common.platform.command.CommandContext
import taboolib.common.platform.command.component.CommandComponent
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock

val locks = ConcurrentHashMap<Int, ReentrantLock>()

/**
 * 异步执行 (带锁以防止出问题)
 */
fun <T> CommandComponent.executeAsync(
    bind: Class<T>,
    function: (sender: T, context: CommandContext<T>, argument: String) -> Unit,
) = this.execute(bind) { sender, context, argument ->
    val locker = locks.computeIfAbsent(function.hashCode()) { ReentrantLock(true) }
    futureRunAsync {
        locker.lock() // 上锁
        // 执行方法 (自动捕获异常)
        try {
            function.invoke(sender, context, argument)
        } catch (ex: Throwable) {
            ex.printStackTrace()
        }
        locker.unlock()// 解锁
    }
}

inline fun <reified T> CommandComponent.executeAsync(noinline function: (sender: T, context: CommandContext<T>, argument: String) -> Unit) {
    executeAsync(T::class.java, function)
}