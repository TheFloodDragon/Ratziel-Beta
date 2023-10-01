package cn.fd.ratziel.core.element.api

import cn.fd.ratziel.core.element.Element
import java.io.File

interface FileElementLoader : ElementLoader {

    fun load(file: File): List<Element>

}