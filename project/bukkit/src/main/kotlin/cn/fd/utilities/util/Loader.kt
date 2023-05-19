package cn.fd.utilities.util

import cn.fd.utilities.config.ConfigYaml
import cn.fd.utilities.module.ModuleLoader
import cn.fd.utilities.module.ModuleLoader.getModuleManager
import cn.fd.utilities.module.ModuleManager
import cn.fd.utilities.module.outdated.Module
import cn.fd.utilities.util.FileListener.unlisten
import taboolib.common.platform.function.console
import taboolib.common.reflect.Reflex.Companion.invokeMethod
import taboolib.module.lang.Language
import taboolib.module.lang.sendLang
import java.io.File
import java.util.function.Consumer

object Loader {

    fun reloadConfigs() {
        ConfigYaml.conf.reload()
    }

    fun reloadAll() {

        ModuleLoader.setModuleManager(ModuleManager(
            arrayListOf<File>().apply {
                ConfigYaml.WORKSPACES_PATHS.forEach {
                    add(File(it))
                }
            }
        )
        )

        //重载语言
        Language.reload()
        //重载所有配置文件
        reloadConfigs()
        //重载所有模块
        //reloadModules()
        //测试
        //BungeeChannel.printServers()
        //TODO 多线程
        getModuleManager().registerAll()
        println(getModuleManager().modules)
        getModuleManager().modules.keys.forEach {
            println(it)
            getModuleManager().getModuleById(it.identifier).printMyself()
        }
    }


    /**
     * 监听文件
     * @param file 要监听的文件
     */
    @Deprecated("后面会改")
    fun <T> listen(file: File, function: Consumer<T> /*() -> Unit*/) {
        //如果未开启多线程，就不监听文件了
        if (!ConfigYaml.MULTI_THREAD) return
        println("测试1")
        //如果未开启多线程，就不监听文件了
        if (!ConfigYaml.MULTI_THREAD) return

        /**
         * 当文件变化时，重新加载模块
         */
        if (!FileListener.isListening(file)) {
            //监听文件变化
            FileListener.listener(file) {
                val start = System.currentTimeMillis()
                try {
                    //执行方法
                    //run(function)
                    function.invokeMethod<T>(String(), null)
                } catch (t: Throwable) {
                    //报告错误日志
                    console().sendLang("Module-Config-Failed", file.name, t.stackTraceToString())
                    return@listener
                }

                //输出日志
                console().sendLang("Module-Config-Reloaded", file.name, System.currentTimeMillis() - start)
            }
        }
    }

    /**
     * 取消监听模块
     */
    @Deprecated("后面会改")
    fun unlisten(module: Module) {
        if (FileListener.isListening(module.file)) unlisten(module.file)
    }


}