package cn.fd.ratziel.core.element.api.loader

import cn.fd.ratziel.core.element.Element
import java.io.File

interface FileElementLoader : ElementLoader {

    fun load(file: File): List<Element>

}