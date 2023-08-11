package cn.fd.utilities.bukkit.loader

import cn.fd.utilities.common.config.Settings
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.module.lang.Language

//TODO 不大喜欢这种方式
object Loader {

    @Awake(LifeCycle.LOAD)
    fun load() {
        //语言设置
        Language.default = Settings.LANGUAGE
        //工作空间
//        initWorkSpace()
    }


//    fun initWorkSpace() {
//        WorkspaceLoader.loadWorkspace(Settings.WORKSPACES_PATHS)
//        debug(WorkspaceLoader.getWorkspaces())
//        debug(WorkspaceLoader.getAllFiles())
//    }


}