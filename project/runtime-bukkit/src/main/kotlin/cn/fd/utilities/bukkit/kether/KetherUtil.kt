package cn.fd.utilities.bukkit.kether

import taboolib.library.kether.ParsedAction
import taboolib.module.kether.ScriptFrame


fun <T> ScriptFrame.getFromFrame(value: ParsedAction<*>?, default: T): T {
    return value?.let {
        newFrame(it).run<T>().getNow(default)
    } ?: default
}