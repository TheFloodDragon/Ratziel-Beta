package cn.fd.ratziel.common.command

import taboolib.common.platform.command.CommandContext
import taboolib.common.platform.command.component.CommandComponent
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock

val commandLocks = ConcurrentHashMap<Int, ReentrantLock>()

/**
 * 异步执行 (带锁以防止出问题)
 */
fun <T> CommandComponent.executeAsync(
    bind: Class<T>,
    function: (sender: T, context: CommandContext<T>, argument: String) -> Unit,
) = this.execute(bind) { sender, context, argument ->
    // 获取锁 (公平锁)
    val lock = commandLocks.computeIfAbsent(function.hashCode()) { ReentrantLock(true) }
    CompletableFuture.runAsync {
        lock.lock() // 上锁
        function.invoke(sender, context, argument) // 执行方法
        lock.unlock()// 解锁
    }.exceptionally { it.printStackTrace(); null }
}

inline fun <reified T> CommandComponent.executeAsync(noinline function: (sender: T, context: CommandContext<T>, argument: String) -> Unit) {
    executeAsync(T::class.java, function)
}