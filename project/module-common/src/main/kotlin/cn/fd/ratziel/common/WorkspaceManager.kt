package cn.fd.ratziel.common

import cn.fd.ratziel.common.config.Settings
import cn.fd.ratziel.common.event.WorkspaceInitializeEvent
import cn.fd.ratziel.core.Workspace
import cn.fd.ratziel.core.util.callThenRun
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
        WorkspaceInitializeEvent(file,copyDefaults).callThenRun {
            // 复制默认文件
            if (copyDefaults && !file.exists()) // 文件夹未创建时
                releaseWorkspace(target = file.name)
            else file.mkdirs()
            workspaces.add(Workspace(file))
        }
    }

    fun initializeWorkspace(path: String, copyDefaults: Boolean = true) {
        initializeWorkspace(File(path), copyDefaults)
    }

    fun initializeWorkspace(workspace: Workspace, copyDefaults: Boolean = true) {
        initializeWorkspace(workspace.path, copyDefaults)
    }

    /**
     * 获取工作空间内过滤后的文件
     */
    fun getFilteredFiles(spaces: Iterable<Workspace> = workspaces) =
        Settings.fileFilter.toRegex().let { mather ->
            getAllFiles(spaces).filter { it.name.matches(mather) }
        }

    /**
     * 获取所有工作空间内的所有文件
     */
    fun getAllFiles(spaces: Iterable<Workspace> = workspaces) =
        spaces.flatMap { it.getFiles() }

    /**
     * 复制默认工作空间内文件到默认工作空间
     * TODO 使用别的方法
     */
    fun releaseWorkspace(files: Array<String> = DefaultFileRegistry.files, target: String) {
        files.forEach {
            releaseResourceFile("${DefaultFileRegistry.PATH}/$it", target = "$target/$it")
        }
    }

}