package cn.fd.utilities.common

import cn.fd.utilities.core.api.Workspace
import java.io.File

object WorkspaceManager {

    val workspaces: MutableSet<Workspace> = mutableSetOf()

    /**
     * 从文件中加载工作空间
     * @param create 是否创建目录如果工作空间目录不存在
     */
    fun registerWorkspace(path: File, create: Boolean = true) {
        workspaces.add(Workspace(path))
        if (create && !path.exists()) path.mkdirs()
    }

    /**
     * 从字符串列表(迭代器)加载工作空间
     * @param create 是否创建目录如果工作空间目录不存在
     */
    fun registerWorkspace(paths: Iterable<String>, create: Boolean = true) {
        paths.forEach { registerWorkspace(File(it), create) }
    }

    /**
     * 获取所有工作空间内的所有文件
     */
    fun getAllFiles(spaces: Iterable<Workspace> = workspaces): List<File> {
        return mutableListOf<File>().also { out ->
            spaces.forEach { ws -> ws.getFiles().forEach { out.add(it) } }
        }
    }

}