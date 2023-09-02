package cn.fd.ratziel.core.element.loader

import cn.fd.ratziel.core.element.Element
import java.io.File

interface FileElementLoader : ElementLoader {

    fun load(file: File): List<Element>

}