package cn.fd.utilities.core.api.element.loader

import cn.fd.utilities.core.api.element.Element
import cn.fd.utilities.core.api.util.ResultFuture
import java.io.File

interface FileElementLoader : ElementLoader {

    fun load(file: File): ResultFuture<Element?>

}