package cn.fd.utilities.common.loader

import cn.fd.utilities.common.WorkspaceManager.getAllFiles
import cn.fd.utilities.common.WorkspaceManager.registerWorkspace
import cn.fd.utilities.common.WorkspaceManager.workspaces
import cn.fd.utilities.common.config.Settings.WORKSPACES_PATHS
import taboolib.common.platform.ProxyCommandSender
import taboolib.module.lang.sendLang

object WorkspaceLoader {

    //@Awake(LifeCycle.LOAD) //在插件加载时加载工作空间

    /**
     * 初始化命名空间
     * 即注册命名空间
     */
    fun init(sender: ProxyCommandSender) {
        WORKSPACES_PATHS.let {
            if (it.isEmpty())
                sender.sendLang("Workspace-Empty")
            else registerWorkspace(it) //注册命名空间
        }
    }

    /**
     * 加载命名空间
     */
    fun load(sender: ProxyCommandSender) {
        init(sender)
        val loader = ElementFileLoader() //创建一个加载器对象
        getAllFiles().forEach {
            loader.load(it)
        }
    }

    /**
     * 重新加载命名空间
     */
    fun reload(sender: ProxyCommandSender) {
        workspaces.clear()
        load(sender)
    }

}