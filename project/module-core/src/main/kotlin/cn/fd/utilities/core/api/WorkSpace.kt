package cn.fd.utilities.core.api

import cn.fd.utilities.util.listFilesDeep
import java.io.File

class WorkSpace(
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

}