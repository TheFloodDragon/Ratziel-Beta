package cn.fd.utilities.core.module.outdated

import taboolib.common.io.newFile
import taboolib.common.platform.function.getDataFolder
import taboolib.module.configuration.Configuration
import java.io.File
import java.util.*

@Deprecated("烂的一批没有用")
abstract class ModuleConfig(val resourcePath: String /*文件资源位置,就是jar包内的文件的路径*/) {

    //根据这个路径初始化文件的储存位置
    open val path: String = File(resourcePath).name

    //配置文件
    lateinit var conf: Configuration

    //文件储存位置
    open lateinit var file: File

    //是否启用此配置文件
    open val enable: Boolean = true

    init {
        init()
    }

    /**
     * 初始化模块配置文件
     * 主要有两个步骤:
     *      1.设置文件路径
     *      2.创建配置文件
     */
    fun init() {
        //初始化文件
        File(getDataFolder(), "module\\$path").also {
            file = it
            it.releaseFile(resourcePath)
        }
    }

    /**
     * 重载方法
     */
    open fun reload() {
        if (enable) {
            init()
            reloadConfig()
        }
    }

    /**
     * 从文件路径重载配置文件
     */
    fun reloadConfig() {
        //从路径获取配置文件
        val config = Configuration.loadFromFile(file)
        //设置模块配置文件
        this.conf = config
    }

    /**
     * 创建配置文件
     * (复制JAR文件中资源路径内的文件到配置文件路径)
     */
    open fun File.releaseFile(resourcePath: String) {
        if (!this.exists()) {
            releaseResourceFile(
                resourcePath,
                this.javaClass.classLoader,
                File("${getDataFolder().path}\\module", resourcePath)
            )
        }
    }

    fun releaseResourceFile(
        path: String,
        classloader: ClassLoader = this.javaClass.classLoader,
        file: File = File(getDataFolder(), path),
        replace: Boolean = true
    ): File {
        if (file.exists() && !replace) {
            return file
        }
        newFile(file).writeBytes(
            classloader.getResourceAsStream(path)?.readBytes()
                ?: error("${if (Locale.getDefault().language == "zh_CN") "找不到资源" else "resource not found"}: $path")
        )
        return file
    }

}