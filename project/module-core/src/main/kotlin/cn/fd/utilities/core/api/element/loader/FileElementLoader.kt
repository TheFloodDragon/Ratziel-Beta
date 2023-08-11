package cn.fd.utilities.core.api.element.loader

import java.io.File

interface FileElementLoader : ElementLoader {

    fun load(file: File): Boolean

}