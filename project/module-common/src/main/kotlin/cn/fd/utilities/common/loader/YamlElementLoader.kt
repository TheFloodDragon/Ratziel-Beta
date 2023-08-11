package cn.fd.utilities.common.loader

import cn.fd.utilities.common.debug
import cn.fd.utilities.core.api.element.Element
import cn.fd.utilities.core.api.element.loader.FileElementLoader
import cn.fd.utilities.core.api.util.ResultFuture
import java.io.File

object YamlElementLoader : FileElementLoader {

    override fun load(file: File): ResultFuture<Element?> {
        debug("Loading $file")
        return ResultFuture<Element?>(null).success()
        //TODO
    }

}