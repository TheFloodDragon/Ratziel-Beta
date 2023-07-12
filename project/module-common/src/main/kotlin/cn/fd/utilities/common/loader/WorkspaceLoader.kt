package cn.fd.utilities.common.loader

import cn.fd.utilities.core.api.Workspace
import java.io.File

object WorkspaceLoader {

    private val workspaces: MutableSet<Workspace> = mutableSetOf()

    /**
     * 获得加载的所有工作空间
     */
    fun getWorkspaces(): Set<Workspace> {
        return this.workspaces
    }

    /**
     * 从文件中加载工作空间
     * @param create 是否创建目录如果工作空间目录不存在
     */
    fun loadWorkspace(path: File, create: Boolean = true) {
        workspaces.add(Workspace(path))
        if (create && !path.exists()) path.createNewFile()
    }

    /**
     * 从字符串列表(迭代器)加载工作空间
     * @param create 是否创建目录如果工作空间目录不存在
     */
    fun loadWorkspace(paths: Iterable<String>, create: Boolean = true) {
        paths.forEach { loadWorkspace(File(it), create) }
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