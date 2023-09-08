package cn.fd.ratziel.common

import cn.fd.ratziel.core.Workspace
import taboolib.common.platform.function.releaseResourceFile
import java.io.File

object WorkspaceManager {

    /**
     * 载入的工作空间
     */
    val workspaces: MutableList<Workspace> = mutableListOf()

    /**
     * 加载工作空间
     *
     * @param file 工作空间路径
     * @param copyDefaults 是否复制默认文件
     */
    fun initializeWorkspace(file: File, copyDefaults: Boolean = true) {
        // 复制默认文件
        if (copyDefaults && file.exists()) // 文件夹未创建时
            releaseWorkspace(target = file.name)
        else file.mkdirs()
        markInitialized(Workspace(file))
    }

    fun initializeWorkspace(path: String, copyDefaults: Boolean = true) {
        initializeWorkspace(File(path), copyDefaults)
    }

    fun initializeWorkspace(workspace: Workspace, copyDefaults: Boolean = true) {
        initializeWorkspace(workspace.path, copyDefaults)
    }

    /**
     * 标记已加载工作空间
     */
    fun markInitialized(wp: Workspace) {
        workspaces.add(wp)
    }

    /**
     * 获取所有工作空间内的所有文件
     */
    fun getAllFiles(spaces: Iterable<Workspace> = workspaces): List<File> {
        return spaces.flatMap { it.getFiles() }
    }

    /**
     * 复制默认工作空间内文件到默认工作空间
     */
    fun releaseWorkspace(files: Array<String> = DefaultFileRegistry.files, target: String) {
        files.forEach {
            releaseResourceFile("${DefaultFileRegistry.PATH}/$it", target = "$target/$it")
        }
    }

}