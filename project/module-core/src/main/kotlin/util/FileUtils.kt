package util

import taboolib.common.platform.PlatformFactory
import tb.common.platform.service.PlatformIO
import java.io.File

/**
 * 深度文件获取
 */
fun File.listFilesDeep(): Iterator<File> {
    return this.walk().filter { it.isFile }.iterator()
}


/**
 * 释放当前插件内的特定资源文件
 *
 * @param resource 资源文件路径
 * @param target 目标文件路径
 * @param replace 是否覆盖文件
 */
fun releaseResourceFile(resource: String, target: String = resource, replace: Boolean = false): File {
    return PlatformFactory.getService<PlatformIO>().releaseResourceFile(resource, target, replace)
}