package cn.fd.utilities.common

import cn.fd.utilities.core.api.Workspace
import cn.fd.utilities.core.util.releaseResourceFile
import java.io.File

object WorkspaceManager {

    val workspaces: MutableSet<Workspace> = mutableSetOf()

    /**
     * 注册工作空间
     */
    fun registerWorkspace(file: File) {
        workspaces.add(Workspace(file))
    }

    /**
     * 获取所有工作空间内的所有文件
     */
    fun getAllFiles(spaces: Iterable<Workspace> = workspaces): List<File> {
        return mutableListOf<File>().also { out ->
            spaces.forEach { ws -> ws.getFiles().forEach { out.add(it) } }
        }
    }

    /**
     * 复制默认工作空间内文件到默认工作空间
     */
    fun releaseWorkspace(target: String) {
        releaseResourceFile("default", target, false)
    }

}