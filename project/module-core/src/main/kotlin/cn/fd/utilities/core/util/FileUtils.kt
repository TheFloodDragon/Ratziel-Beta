package cn.fd.utilities.core.util

import java.io.File

/**
 * 深度文件获取
 */
fun File.listFilesDeep(): Iterator<File> {
    return this.walk().filter { it.isFile }.iterator()
}