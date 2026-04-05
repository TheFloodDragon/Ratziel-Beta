package cn.fd.ratziel.common

import cn.fd.ratziel.common.config.Settings
import cn.fd.ratziel.core.contextual.toAttachedProperties
import cn.fd.ratziel.core.element.ElementConfiguration
import cn.fd.ratziel.core.util.JarUtil
import cn.fd.ratziel.core.util.toJsonElement
import taboolib.common.io.newFile
import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.getJarFile
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.configuration.Configuration
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
     * 初始化所有工作空间
     */
    fun initializeAllWorkspace() {
        workspaces.clear()
        val conf = Settings.conf.getConfigurationSection("Workspaces") ?: return
        conf.getKeys(false)
            .mapNotNullTo(workspaces) { key ->
                conf.getConfigurationSection(key)?.let { initializeWorkspace(it, true) }
            }
    }

    /**
     * 初始化工作空间
     *
     * @param settings 工作空间设置
     * @param copyDefaults 是否复制默认文件
     */
    fun initializeWorkspace(settings: ConfigurationSection, copyDefaults: Boolean = true): Workspace {
        val folder = File(settings.getString("path", defaultPath)!!)
        if (copyDefaults && !folder.exists()) {
            releaseDefaultWorkspace(folder)
        } else {
            folder.mkdirs()
        }
        val json = Configuration.loadFromOther(settings).toJsonElement()
        return Workspace(folder, ElementConfiguration(listOf(json.toAttachedProperties(ElementConfiguration.GROUP))))
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
