package cn.fd.utilities.common.impl

import cn.fd.utilities.common.loader.ConfigFileLoader
import cn.fd.utilities.core.element.Element
import cn.fd.utilities.core.element.loader.FileElementLoader
import java.io.File

/**
 * DefaultFileElementLoader
 *
 * @author TheFloodDragon
 * @since 2023/8/22 16:38
 */
object DefaultFileElementLoader : FileElementLoader {

    override fun load(file: File): Element? {
        // 获取 Config
        val config = ConfigFileLoader.loadFromFile(file)
        TODO("Not yet implemented")
    }

}