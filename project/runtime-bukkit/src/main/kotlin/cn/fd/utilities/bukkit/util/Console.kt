package cn.fd.utilities.bukkit.util

import org.bukkit.command.CommandSender
import taboolib.common.platform.function.console
import taboolib.platform.util.asLangText

inline fun <T, R> T.alert(block: (T) -> R): R? {
    return alertBlock { block(this) }.getOrNull()
}

inline fun <R> alertBlock(block: () -> R): Result<R> {
    return try {
        Result.success(block())
    } catch (e: Throwable) {
        e.prettyPrint()
        Result.failure(e)
    }
}

fun Throwable.prettyPrint(head: Boolean = true) {
    if (head) println(console().cast<CommandSender>().asLangText("throwable-print"))
    println("§8${javaClass.name}")
    println("§c$localizedMessage")


    stackTrace
        .filter { "taboolib" in it.toString() || "invero" in it.toString() }
        .forEach {
            val info =
                it.toString().split("//").let { split ->
                    split.getOrNull(1) ?: split.first()
                }
            println(" §8$info")
        }

    cause?.let {
        println("§6Caused by: ")
        it.prettyPrint(false)
    }
}