package cn.fd.utilities.common.loader

import cn.fd.utilities.common.config.Settings
import taboolib.common.LifeCycle
import taboolib.common.io.newFile
import taboolib.common.platform.Awake
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.function.console
import taboolib.module.lang.sendLang
import kotlin.system.measureTimeMillis
import cn.fd.utilities.common.WorkspaceManager as wsm

object WorkspaceLoader {

    /**
     * 注册工作空间
     * @param create 是否创建目录如果工作空间目录不存在
     */
    fun init(sender: ProxyCommandSender, create: Boolean = true) {
        /**
         * 初始化工作空间
         */
        measureTimeMillis {
            Settings.WORKSPACES_PATHS.forEach { path ->
                wsm.registerWorkspace(newFile(path, create, true)) //注册命名空间
            }
            // 复制默认文件
            Settings.defaultWorkspace.let {
                if (it.exists() && it.list()?.size == 0) //当文件夹创建了并且文件夹内没有文件时
                    wsm.releaseWorkspace(target = it.name)
            }
        }.let {
            sender.sendLang("Workspace-Inited", wsm.workspaces.size, it)
        }
    }

    /**
     * 加载命名空间 (在插件加载时)
     */
    fun load(sender: ProxyCommandSender) {
        /**
         * 加载元素文件
         */
        val loader = DefaultElementLoader() //创建一个加载器对象
        measureTimeMillis {
            wsm.getAllFiles().map {
                loader.load(it)
            }
        }.let {
            sender.sendLang("Workspace-Finished", loader.getCount(), it)
        }
    }

    /**
     * 重新加载命名空间
     */
    fun reload(sender: ProxyCommandSender) {
        wsm.workspaces.clear()
        //初始化工作空间
        this.init(sender)
        //加载元素文件
        this.load(sender)
    }

    /**
     * 在插件加载时注册并加载命名空间
     */
    @Awake(LifeCycle.LOAD)
    fun run() {
        this.init(console())
        this.load(console())
    }

}