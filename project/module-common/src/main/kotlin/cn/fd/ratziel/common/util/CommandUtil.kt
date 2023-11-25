package cn.fd.ratziel.common.util

import cn.fd.ratziel.core.util.quickRunFuture
import taboolib.common.platform.command.CommandContext
import taboolib.common.platform.command.component.CommandComponent

fun <T> CommandComponent.executeAsync(
    bind: Class<T>,
    function: (sender: T, context: CommandContext<T>, argument: String) -> Unit,
) = this.execute(bind) { sender, context, argument ->
    quickRunFuture { function.invoke(sender, context, argument) }
}

inline fun <reified T> CommandComponent.executeAsync(noinline function: (sender: T, context: CommandContext<T>, argument: String) -> Unit) {
    executeAsync(T::class.java, function)
}