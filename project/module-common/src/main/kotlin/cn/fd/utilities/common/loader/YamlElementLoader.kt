package cn.fd.utilities.common.loader

import cn.fd.utilities.common.debug
import cn.fd.utilities.core.api.element.loader.FileElementLoader
import java.io.File

object YamlElementLoader : FileElementLoader {

    override fun load(file: File): Boolean {
        debug("Loading $file")
        return true
        //TODO
    }

}