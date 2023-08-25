package cn.fd.utilities.common.loader

import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.function.console
import taboolib.module.lang.asLangText

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
    if (head) println(console().cast<ProxyCommandSender>().asLangText("throwable-print"))
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