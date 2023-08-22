package cn.fd.utilities.core.element.loader

import cn.fd.utilities.core.element.Element
import java.io.File

interface FileElementLoader : ElementLoader {

    fun load(file: File): Element?

}