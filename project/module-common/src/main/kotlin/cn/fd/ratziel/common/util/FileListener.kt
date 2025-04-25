package cn.fd.ratziel.common.util

import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common5.FileWatcher
import java.io.File
import java.util.concurrent.ConcurrentSkipListSet
import java.util.function.Consumer

/**
 * FileListener
 *
 * @author TheFloodDragon
 * @since 2025/4/25 20:23
 */
object FileListener {

    /**
     * 监听中的文件列表
     */
    private val listening = ConcurrentSkipListSet<File>()

    /**
     * 文件观察者
     */
    private val watcher = FileWatcher(500)

    /**
     * 监听文件
     */
    fun listen(file: File, callback: Consumer<File>) {
        watcher.addSimpleListener(file, callback)
        listening.add(file)
    }

    /**
     * 取消监听文件
     */
    fun unlisten(file: File) {
        watcher.removeListener(file)
    }

    /**
     * 清空监听列表
     */
    fun clear() {
        for (f in listening) {
            watcher.removeListener(f)
        }
        listening.clear()
    }

    @Awake(LifeCycle.DISABLE)
    private fun dispose() {
        watcher.release()
    }

}