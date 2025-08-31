package cn.fd.ratziel.core.util

import java.io.File

/**
 * 解析文件相对路径, 但是如果 [relative] 以 ! 开头, 则直接使用绝对路径
 */
fun File.resolveOrAbsolute(relative: String): File =
    if (relative.startsWith("!")) {
        File(relative.drop(1))
    } else this.resolve(relative)
