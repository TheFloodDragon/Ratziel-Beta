package cn.fd.utilities.core.element.api.loader

import cn.fd.utilities.core.element.Element
import java.io.File
import java.util.concurrent.CompletableFuture

interface FileElementLoader : ElementLoader {

    fun load(file: File): CompletableFuture<Set<Element>>

}