package cn.fd.ratziel.bukkit.loader

import cn.fd.ratziel.common.config.Settings
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.module.lang.Language

object Loader {

    @Awake(LifeCycle.LOAD)
    fun load() {
        //语言设置
        Language.default = Settings.Language
        //工作空间
//        initWorkSpace()
    }


//    fun initWorkSpace() {
//        WorkspaceLoader.loadWorkspace(Settings.WORKSPACES_PATHS)
//        debug(WorkspaceLoader.getWorkspaces())
//        debug(WorkspaceLoader.getAllFiles())
//    }


}