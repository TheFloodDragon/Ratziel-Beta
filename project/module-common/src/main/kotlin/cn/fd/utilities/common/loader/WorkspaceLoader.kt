package cn.fd.utilities.common.loader

import cn.fd.utilities.common.WorkspaceManager
import cn.fd.utilities.common.WorkspaceManager.getAllFiles
import cn.fd.utilities.common.WorkspaceManager.registerWorkspace
import cn.fd.utilities.common.WorkspaceManager.workspaces
import cn.fd.utilities.common.config.Settings
import cn.fd.utilities.common.config.Settings.WORKSPACES_PATHS
import cn.fd.utilities.common.log
import taboolib.common.LifeCycle
import taboolib.common.io.newFile
import taboolib.common.platform.Awake
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.function.console
import taboolib.module.lang.sendLang
import kotlin.system.measureNanoTime

object WorkspaceLoader {

    //@Awake(LifeCycle.LOAD) //在插件加载时加载工作空间

    /**
     * 注册工作空间
     * @param create 是否创建目录如果工作空间目录不存在
     */
    fun init(create: Boolean = true) {
        WORKSPACES_PATHS.forEach { path ->
            log(path)
            registerWorkspace(newFile(path, create, true)) //注册命名空间
            // 复制默认文件
            Settings.defaultWorkspace.let {
                if (it.exists() && it.list() == null) //当文件夹创建了并且文件夹内没有文件时
                    WorkspaceManager.releaseWorkspace(it.path)
            }
        }
    }

    /**
     * 加载命名空间 (在插件加载时)
     */
    @Awake(LifeCycle.LOAD)
    fun load(sender: ProxyCommandSender = console()) {
        init()
        val loader = DefaultElementLoader() //创建一个加载器对象
        val elapsed = measureNanoTime {
            getAllFiles().forEach {
                loader.load(it)
            }
        }
        sender.sendLang("Workspace-Finished", loader.getCount(), elapsed)
    }

    /**
     * 重新加载命名空间
     */
    fun reload(sender: ProxyCommandSender) {
        workspaces.clear()
        load(sender)
    }

}