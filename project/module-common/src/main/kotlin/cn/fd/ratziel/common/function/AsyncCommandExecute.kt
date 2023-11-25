package cn.fd.ratziel.common.function

import cn.fd.ratziel.core.function.quickRunFuture
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
    val id = function.hashCode()
    val locker = locks.computeIfAbsent(id) { ReentrantLock(true) }
    locker.lock() // 上锁
    quickRunFuture { function.invoke(sender, context, argument) }
        .thenRun { locker.unlock() } // 运行完解锁
}

inline fun <reified T> CommandComponent.executeAsync(noinline function: (sender: T, context: CommandContext<T>, argument: String) -> Unit) {
    executeAsync(T::class.java, function)
}