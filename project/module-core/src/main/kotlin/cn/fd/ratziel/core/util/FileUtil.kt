package cn.fd.ratziel.core.util

import java.io.File

/**
 * 深度文件获取
 */
fun File.listFilesDeep(): Sequence<File> {
    return this.walk().filter { it.isFile }
}