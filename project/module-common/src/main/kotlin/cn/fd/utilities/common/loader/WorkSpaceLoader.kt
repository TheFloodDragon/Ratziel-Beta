package cn.fd.utilities.common.loader

import cn.fd.utilities.core.api.WorkSpace
import java.io.File

object WorkSpaceLoader {

    /**
     * 获取工作空间
     * @param paths 工作空间路径的字符串表现
     */
    fun getWorkSpaces(paths: Iterable<String>): List<WorkSpace> {
        return paths.map { WorkSpace(File(it)) }
    }

}