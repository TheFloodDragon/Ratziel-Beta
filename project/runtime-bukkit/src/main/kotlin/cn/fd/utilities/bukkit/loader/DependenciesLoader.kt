package cn.fd.utilities.bukkit.loader

import cn.fd.utilities.env.BukkitEnv
import cn.fd.utilities.env.CommonEnv
import taboolib.common.env.RuntimeEnv
import taboolib.common.platform.Awake

//感觉有点小题大做
object DependenciesLoader {

    /**
     * 加载依赖项
     */
    @Awake
    fun loadDependency() {
        RuntimeEnv.ENV.loadDependency(CommonEnv::class.java, true)
        RuntimeEnv.ENV.loadDependency(BukkitEnv::class.java, true)
    }

}