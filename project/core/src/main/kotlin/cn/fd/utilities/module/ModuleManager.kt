package cn.fd.utilities.module

import cn.fd.utilities.module.ModuleLoader.findModulesInDirs
import cn.fd.utilities.module.ModuleLoader.register
import cn.fd.utilities.module.ModuleLoader.unregister
import taboolib.common.io.getInstance
import taboolib.common.platform.function.console
import taboolib.module.lang.sendLang
import java.io.File
import java.nio.file.Path
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock

class ModuleManager(
    private val workspacePaths: List<File>,
    private val isMultiThread: Boolean = true
) {

    /**
     * 储存着加载的所有模块
     * String 模块名称
     * ModuleExpansion 模块对象
     */
    private var modules: MutableMap<ModuleInfo, ModuleExpansion> = ConcurrentHashMap()

    fun getWorkspaces(): List<File> {
        return workspacePaths
    }

    //获取所有模块
    fun getModules(): MutableMap<ModuleInfo, ModuleExpansion> {
        return this.modules
    }

    //获取启用的所有模块
    fun getEnabledModules(): Map<ModuleInfo, ModuleExpansion> {
        return this.modules.filter { it.key.isEnabled }
    }

    //通过标识符找到模块
    fun getModuleById(identifier: String): ModuleExpansion {
        return modules.filter { it.key.identifier == identifier }.values.first().javaClass.getInstance(true)!!.get()
    }

    //通过路径找到模块
    fun getModuleByPath(path: Path): ModuleExpansion {
        return modules.filter { it.key.filePath == path }.values.first().javaClass.getInstance(true)!!.get()
    }

    //模块锁
    private val modulesLock = ReentrantLock()

    /**
     * 注册所有模块
     * @param sender 发送消息的命令发送者(就是注册模块的消息提示发给谁)
     */
    fun registerAll() {
        val run = {
            console().sendLang("Module-Loader-Loading")

            val registered = findModulesInDirs(getWorkspaces()).map {
                it.getInstance(true)?.get()?.register()
            }

            console().sendLang("Module-Loader-Finished", registered.size)
        }
        //如果开启多线程，就创建一个新线程用来加载类
        if (isMultiThread) {
            Thread {
                modulesLock.lock()
                run()
                modulesLock.unlock()
            }.apply { name = "Module-Loader" }.start()
        } else run()

    }

    /**
     * 卸载所有模块
     */
    fun unregisterAll() {
        for (module in modules.values) {
            module.unregister()
        }
        modules.clear()
    }

}
