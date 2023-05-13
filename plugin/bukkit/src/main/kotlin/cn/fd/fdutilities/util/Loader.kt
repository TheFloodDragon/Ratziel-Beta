package cn.fd.fdutilities.util

import cn.fd.fdutilities.channel.BungeeChannel
import cn.fd.fdutilities.config.ConfigYaml
import cn.fd.fdutilities.config.SettingsYaml
import cn.fd.fdutilities.module.ModuleManager
import cn.fd.fdutilities.module.outdated.Module
import cn.fd.fdutilities.module.outdated.PlaceholderAPIExtension
import cn.fd.fdutilities.module.outdated.ServerTeleportModule
import cn.fd.fdutilities.util.FileListener.unlisten
import org.bukkit.Bukkit
import taboolib.common.platform.function.console
import taboolib.common.reflect.Reflex.Companion.invokeMethod
import taboolib.module.lang.*
import java.io.File
import java.util.function.Consumer

object Loader {


    fun reloadModules() {
        arrayOf(
            ServerTeleportModule, PlaceholderAPIExtension
        ).forEach {
            it.reload()
            //检测是否启用，如果不启用，就不需要监听文件变化
            if (it.isEnabled) {
                //监听此文件
                //listen(it.file, it)
//                listen(it.file) {
//                    it.reload()
//                }
                listen<Module>(it.file){
                }
            } else {
                unlisten(it)
            }
        }

        //重新加载所有模块
        ModuleManager.apply {
            unregisterAll()
            registerAll(Bukkit.getConsoleSender())
            modules.values.forEach {
                it.reload()
            }
        }
    }


    fun reloadConfigs() {
        SettingsYaml.conf.reload()
        ConfigYaml.conf.reload()
    }

    fun reloadAll() {
        //重载语言
        Language.reload()
        //重载所有配置文件
        reloadConfigs()
        //重载所有模块
        reloadModules()
        //测试
        BungeeChannel.printServers()
        ModuleManager.registerAll(Bukkit.getConsoleSender())
        for (module in ModuleManager.modules) {
            module.value.printMyself()
        }
    }

    /**
     * 监听文件
     * @param file 要监听的文件
     */
    fun <T> listen(file: File, function: Consumer<T> /*() -> Unit*/) {
        //如果未开启多线程，就不监听文件了
        if (!SettingsYaml.MULTI_THREAD)
            return
        println("测试1")
        //如果未开启多线程，就不监听文件了
        if (!SettingsYaml.MULTI_THREAD)
            return

        /**
         * 当文件变化时，重新加载模块
         */
        if (!FileListener.isListening(file)) {
            //监听文件变化
            FileListener.listener(file) {
                val start = System.currentTimeMillis()
                println("测试2")
                try {
                    println("测试3")
                    //执行方法
                    //run(function)
                    function.invokeMethod<T>(String(),null)
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
    fun unlisten(module: Module) {
        if (FileListener.isListening(module.file)) unlisten(module.file)
    }


}