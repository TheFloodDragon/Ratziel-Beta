package cn.fd.ratziel.common

import cn.fd.ratziel.common.config.Settings
import cn.fd.ratziel.common.element.ElementMatcher
import cn.fd.ratziel.core.util.JarUtil
import taboolib.common.io.newFile
import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.getJarFile
import taboolib.library.configuration.ConfigurationSection
import java.io.File
import java.util.jar.JarFile

/**
 * WorkspaceManager
 *
 * @author TheFloodDragon
 * @since 2025/4/12 23:22
 */
object WorkspaceManager {

    /**
     * 载入的工作空间
     */
    val workspaces: MutableList<Workspace> = mutableListOf()

    /**
     * 默认工作空间路径
     */
    private val defaultPath by lazy { "${getDataFolder()}/workspace" }

    /**
     * 默认文件过滤
     */
    private val defaultFilter = "^(?![#!]).*\\.(?i)(yaml|yml|toml|tml|json|conf)$".toRegex()

    /**
     * 初始化所有工作空间
     */
    fun initializeAllWorkspace() {
        // 清空工作空间
        workspaces.clear()
        // 读取配置
        val conf = Settings.conf.getConfigurationSection("Workspaces") ?: return
        for (key in conf.getKeys(false)) {
            val settings = conf.getConfigurationSection(key) ?: continue
            val workspace = initializeWorkspace(settings, true) // 初始化单个工作空间
            workspaces.add(workspace) // 注册工作空间
        }
    }

    /**
     * 初始化工作空间
     *
     * @param settings 工作空间设置
     * @param copyDefaults 是否复制默认文件
     */
    fun initializeWorkspace(settings: ConfigurationSection, copyDefaults: Boolean = true): Workspace {
        // 读取路径
        val folder = File(settings.getString("path", defaultPath)!!)
        // 文件夹未创建时, 复制默认文件
        if (copyDefaults && !folder.exists()) {
            releaseDefaultWorkspace(folder)
        } else folder.mkdirs()
        // 过滤器
        val filter = settings.getString("filter")?.toRegex() ?: defaultFilter
        // 是否监听
        val listen = settings.getBoolean("listen", true)
        // 使用文件名称
        val useFileName = settings.getBoolean("use-filename", false)
        // 统一元素类型
        val unifiedType = settings.getString("unified-type")
            ?.takeUnless { it.equals("None", true) }
            ?.let { ElementMatcher.matchTypeOrNull(it) }
        // 创建工作空间
        return Workspace(folder, filter, listen, useFileName, unifiedType)
    }

    /**
     * 复制默认工作空间内文件到默认工作空间
     */
    private fun releaseDefaultWorkspace(folder: File) {
        val jar = JarFile(getJarFile())
        JarUtil.findInJar(jar) {
            !it.isDirectory && it.name.startsWith("default/")
        }.forEach {
            newFile(File(folder, it.name.substringAfter('/'))).writeBytes(jar.getInputStream(it).readBytes())
        }
    }

}