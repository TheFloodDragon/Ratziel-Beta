package cn.fd.utilities.core.element.loader

import cn.fd.utilities.core.element.Element
import cn.fd.utilities.core.util.ResultFuture
import java.io.File

interface FileElementLoader : ElementLoader {

    fun load(file: File): ResultFuture<Element?>

}