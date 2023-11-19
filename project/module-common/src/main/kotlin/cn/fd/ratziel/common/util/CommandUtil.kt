package cn.fd.ratziel.common.util

import cn.fd.ratziel.core.util.quickRunFuture
import taboolib.common.platform.command.CommandContext
import taboolib.common.platform.command.component.CommandComponent

fun <T> CommandComponent.executeFuture(
    bind: Class<T>,
    function: (sender: T, context: CommandContext<T>, argument: String) -> Unit,
) = this.execute(bind) { sender, context, argument ->
    quickRunFuture { function.invoke(sender, context, argument) }
}

inline fun <reified T> CommandComponent.executeFuture(noinline function: (sender: T, context: CommandContext<T>, argument: String) -> Unit) {
    executeFuture(T::class.java, function)
}
