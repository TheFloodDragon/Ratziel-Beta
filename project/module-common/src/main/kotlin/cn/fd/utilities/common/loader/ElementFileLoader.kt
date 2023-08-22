package cn.fd.utilities.common.loader

import cn.fd.utilities.core.element.Element
import cn.fd.utilities.core.element.loader.FileElementLoader
import java.io.File

class DefaultElementLoader {

    private var counter = 0

    fun load(file: File): Element? {
        file.apply {
//            return when (extension) {
//                "yaml", "yml" -> null//runLoader(YamlElementLoader)
//                else -> null
//            }

            return null
        }
    }

    private fun File.runLoader(loader: FileElementLoader): Element? {
        return loader.load(this).also {
            if (it != null) count()
        }
    }

    fun getCount(): Int {
        return counter
    }

    private fun count() {
        counter += 1
    }

}