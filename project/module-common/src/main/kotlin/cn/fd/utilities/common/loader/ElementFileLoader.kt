package cn.fd.utilities.common.loader

import cn.fd.utilities.common.debug
import cn.fd.utilities.core.api.element.loader.FileElementLoader
import java.io.File

class DefaultElementLoader {

    val count = 0

    fun loadFromFile(file: File) {
        when (file.extension) {
            "yaml", "yml" -> YamlElementLoader.load(file)
        }
    }

    fun runLoader(loader: FileElementLoader) {
        loader.load()
    }

}

object YamlElementLoader : FileElementLoader {

    override fun load(file: File): Boolean {
        debug("Loading $file")
        return true
        //TODO
    }

}