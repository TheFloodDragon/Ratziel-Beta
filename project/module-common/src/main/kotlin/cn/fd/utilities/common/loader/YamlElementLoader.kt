package cn.fd.utilities.common.loader

import cn.fd.utilities.common.api.DefaultElementMatcher
import cn.fd.utilities.common.debug
import cn.fd.utilities.common.log
import cn.fd.utilities.core.element.Element
import cn.fd.utilities.core.element.loader.FileElementLoader
import cn.fd.utilities.core.util.ResultFuture
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Type
import java.io.File

object YamlElementLoader : FileElementLoader {

    override fun load(file: File): ResultFuture<Element?> {
        debug("Loading $file")
        //TODO 什么LJ东西
        try {
            val config = Configuration.loadFromFile(file, Type.YAML)
            debug(config)
            DefaultElementMatcher.match(config)
            return ResultFuture<Element?>(null).success()
        } catch (e: Exception) {
            e.printStackTrace()
            log("Unable to load configuration form file $file!")
        }
        return ResultFuture<Element?>(null).failure()
    }

}