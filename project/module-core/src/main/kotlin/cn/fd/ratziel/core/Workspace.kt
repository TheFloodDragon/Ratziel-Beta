package cn.fd.ratziel.core

import cn.fd.ratziel.core.util.listFilesDeep
import java.io.File

class Workspace(
    /**
     * 工作空间路径
     */
    val path: File,
) {

    /**
     * 获取工作空间中的文件
     */
    fun getFiles(): Sequence<File> {
        return path.listFilesDeep()
    }

    override fun toString(): String {
        return "Workspace{path=$path}"
    }

}