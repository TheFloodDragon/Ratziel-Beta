package cn.fd.utilities.common.loader

import cn.fd.utilities.core.api.element.loader.FileElementLoader
import java.io.File

class DefaultElementLoader {

    //TODO (是否要返回元素)

    private var counter = 0

    fun load(file: File) {
        file.apply {
            when (extension) {
                "yaml", "yml" -> runLoader(YamlElementLoader)
            }
        }
    }

    fun File.runLoader(loader: FileElementLoader) {
        count(loader.load(this))
    }

    fun getSuccesses(): Int {
        return counter
    }

    private fun count(success: Boolean = true) {
        if (success) counter += 1
    }

}