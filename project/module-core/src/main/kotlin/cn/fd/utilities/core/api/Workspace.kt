package cn.fd.utilities.core.api

import util.listFilesDeep
import java.io.File

class Workspace(
    /**
     * 工作空间路径
     */
    val path: File
) {

    /**
     * 获取工作空间中的文件
     */
    fun getFiles(): Iterator<File> {
        return path.listFilesDeep()
    }

    override fun toString(): String {
        return "Workspace Path: $path"
    }

}