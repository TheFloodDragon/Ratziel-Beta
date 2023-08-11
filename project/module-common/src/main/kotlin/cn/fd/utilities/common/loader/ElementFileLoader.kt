package cn.fd.utilities.common.loader

import cn.fd.utilities.core.api.element.Element
import cn.fd.utilities.core.api.element.loader.FileElementLoader
import cn.fd.utilities.core.api.util.ResultFuture
import java.io.File

class DefaultElementLoader {

    //TODO (是否要返回元素)

    private var counter = 0

    fun load(file: File): ResultFuture<Element?> {
        file.apply {
            return when (extension) {
                "yaml", "yml" -> runLoader(YamlElementLoader)
                else -> ResultFuture(null)
            }
        }
    }

    private fun File.runLoader(loader: FileElementLoader): ResultFuture<Element?> {
        loader.load(this).let {
            count(it.getResultB())
            return it
        }
    }

    fun getSuccesses(): Int {
        return counter
    }

    private fun count(success: Boolean = true) {
        if (success) counter += 1
    }

}