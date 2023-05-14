package cn.fd.utilities.module

import cn.fd.utilities.config.ConfigYaml
import cn.fd.utilities.module.ModuleLoader.createInstance
import cn.fd.utilities.module.ModuleLoader.register
import cn.fd.utilities.module.ModuleLoader.unregister
import org.bukkit.command.CommandSender
import taboolib.common.platform.function.getDataFolder
import taboolib.platform.util.sendLang
import java.io.File
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock
import java.util.stream.Collectors

object ModuleManager {

    /**
     * 储存着加载的所有模块
     * String 模块名称
     * ModuleExpansion 模块类
     */
    private var modules: MutableMap<String, ModuleExpansion> = ConcurrentHashMap()

    fun getWorkspaces(): List<File> {
        return arrayListOf<File>().apply {
            ConfigYaml.WORKSPACES_PATHS.forEach {
                add(File(it))
            }
        }
    }

    fun getEnabledModules(): MutableMap<String, ModuleExpansion> {
        return this.modules
    }

    fun getModule(identifier: String): ModuleExpansion? {
        return modules[identifier]
    }


    private const val MODULE_FOLDER = "module"

    //模块文件存放目录
    val folder = File(getDataFolder(), MODULE_FOLDER)

    /**
     * 储存着启用的所有模块
     * String 模块名称
     * ModuleExpansion 模块类
     */
    //var modules: MutableMap<String, ModuleExpansion> = ConcurrentHashMap()

    //模块锁
    private val modulesLock = ReentrantLock()


    private val ABSTRACT_MODULE_METHODS =
        Arrays.stream(ModuleExpansion::class.java.declaredMethods).filter { method: Method ->
            Modifier.isAbstract(method.modifiers)
        }.map { method: Method ->
            MethodSignature(method.name, method.parameterTypes)
        }.collect(Collectors.toSet())

    /**
     * 注册所有模块
     * @param sender 发送消息的命令发送者(就是注册模块的消息提示发给谁)
     */
    fun registerAll(sender: CommandSender) {
        val run = {
            sender.sendLang("Module-Loader-Loading")
//            val registered = ModuleLoader.findModules().stream().filter { obj: Class<out ModuleExpansion?>? ->
//                Objects.nonNull(obj)
//            }.map { clazz: Class<out ModuleExpansion?>? ->
//                clazz?.let {
//                    register(it)
//                }
//            }.filter { obj: Optional<ModuleExpansion> -> obj.isPresent }
//                .map { obj: Optional<ModuleExpansion> -> obj.get() }.collect(Collectors.toList())
            val registered = ModuleLoader.findModulesInDirs(getWorkspaces()).map {
                it?.createInstance()?.register()
            }

            sender.sendLang("Module-Loader-Finished", registered.size)
        }
        //如果开启多线程，就创建一个新线程用来加载类
        if (ConfigYaml.MULTI_THREAD) {
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
