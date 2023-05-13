package cn.fd.fdutilities.module.outdated

import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.releaseResourceFile
import taboolib.module.configuration.Configuration
import java.io.File

@Deprecated("过时")
abstract class Module {

    abstract val resourcePath: String

    abstract var path: String

    var isEnabled: Boolean = true

    abstract var conf: Configuration

    //初始化方法
    abstract fun init()

    //重载方法
    abstract fun onReload()

    lateinit var file: File

    open fun reload() {
        file = File(getDataFolder(), "module\\$path")

        //调用模块的初始化的方法(模块不启用也会执行)
        init()

        if (isEnabled) {
            //初始化配置文件
            reloadConfig()
            //调用模块的重载时的方法(要模块启用才能重载)
            onReload()
        }
    }

    /**
     * 重新读取配置文件
     */
    fun reloadConfig() {
        //创建配置文件
        if (!file.exists()) {
            releaseResourceFile(resourcePath, replace = true)
        }
        //从路径获取配置文件
        val config = Configuration.loadFromFile(file)
        //设置模块配置文件
        this.conf = config
    }

}