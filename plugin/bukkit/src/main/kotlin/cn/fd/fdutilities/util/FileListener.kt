package cn.fd.fdutilities.util

import taboolib.common5.FileWatcher
import java.io.File


/**
 * @author MC~蛟龙
 * qq: 1610105206
 * @date 2022/6/15
 */
object FileListener {

    private val listening = mutableSetOf<File>()

    fun isListening(file: File): Boolean {
        return watcher.hasListener(file)
    }

    fun listener(file: File, runFirst: Boolean = true, runnable: () -> Unit) {
        watcher.addSimpleListener(file, runnable, runFirst)
        listening.add(file)
    }

    fun unlisten(file: File) {
        watcher.removeListener(file)
        listening.remove(file)
    }

    fun clear() {
        var count = 0
        listening.removeIf {
            val remove = !it.exists()
            if (remove) {
                watcher.removeListener(it)
                count++
            }
            remove
        }
        if (count > 0) {
            println("DEBUG: CLEARED $count unused listeners")
        }
    }

    fun uninstall() {
        watcher.unregisterAll()
    }

    val watcher = FileWatcher()

}