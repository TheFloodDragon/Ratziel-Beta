package cn.fd.ratziel.common

import cn.fd.ratziel.common.config.Settings
import cn.fd.ratziel.core.Workspace
import cn.fd.ratziel.core.util.findInJar
import taboolib.common.io.newFile
import taboolib.common.platform.function.getJarFile
import java.io.File

object WorkspaceManager {

    /**
     * 载入的工作空间
     */
    val workspaces: MutableList<Workspace> = mutableListOf()

    /**
     * 加载工作空间
     *
     * @param folder 工作空间路径
     * @param copyDefaults 是否复制默认文件
     */
    fun initializeWorkspace(folder: File, copyDefaults: Boolean = true) {
        // 复制默认文件
        if (copyDefaults && !folder.exists()) // 文件夹未创建时
            releaseWorkspace(folder)
        else folder.mkdirs()
        workspaces.add(Workspace(folder))
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
    fun getAllFiles(spaces: Iterable<Workspace> = workspaces) = spaces.flatMap { it.getFiles() }

    // 默认资源路径
    const val DEFAULT_PATH = "default"

    /**
     * 复制默认工作空间内文件到默认工作空间
     */
    fun releaseWorkspace(folder: File) {
        findInJar(getJarFile()) {
            !it.isDirectory && it.name.startsWith("$DEFAULT_PATH/")
        }.forEach {
            newFile(File(folder, it.first.name.substringAfter('/'))).writeBytes(it.second.readBytes())
        }
    }

}