package cn.fd.fdutilities.module

import cn.fd.fdutilities.FDUtilities
import cn.fd.fdutilities.config.SettingsYaml
import cn.fd.fdutilities.util.ClassUtil
import org.bukkit.command.CommandSender
import org.jetbrains.annotations.ApiStatus
import taboolib.common.platform.function.console
import taboolib.common.platform.function.getDataFolder
import taboolib.module.lang.Language
import taboolib.module.lang.sendLang
import taboolib.platform.util.sendLang
import java.io.File
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock
import java.util.stream.Collectors


object ModuleManager{

    private const val MODULE_FOLDER = "module"

    //模块文件存放目录
    val folder = File(getDataFolder(), MODULE_FOLDER)

    /**
     * 储存着启用的所有模块
     * String 模块名称
     * ModuleExpansion 模块类
     */
    var modules: MutableMap<String, ModuleExpansion> = ConcurrentHashMap()

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
            val registered = findModulesOnDisk().stream().filter { obj: Class<out ModuleExpansion?>? ->
                Objects.nonNull(obj)
            }.map { clazz: Class<out ModuleExpansion?>? ->
                clazz?.let {
                    register(it)
                }
            }.filter { obj: Optional<ModuleExpansion> -> obj.isPresent }
                .map { obj: Optional<ModuleExpansion> -> obj.get() }.collect(Collectors.toList())
            sender.sendLang("Module-Loader-Finished", registered.size)
        }
        //如果开启多线程，就创建一个新线程用来加载类
        if (SettingsYaml.MULTI_THREAD) {
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
    fun unregisterAll(){
        for (module in modules.values) {
            module.unregister()
        }
        modules.clear()
    }

    /**
     * 从模块文件夹内寻找所有文件
     */
    private fun findModulesOnDisk(): List<Class<out ModuleExpansion?>?> {
        /*
          获取模块文件下所有JAR后缀的文件
          如果一个文件都没有，就返回一个空的列表
         */
        val files: Array<File> = folder.listFiles { _, name: String ->
            name.endsWith(
                ".jar"
            )
        } ?: return emptyList()
        //返回所有文件内模块扩展类的集合
        return Arrays.stream(files).map { file: File? ->
            findModuleInFile(file!!)
        }.collect(Collectors.toList())
    }

    /**
     * 从单个文件中寻找模块扩展类
     * @param file 要被寻找的单个文件
     */
    private fun findModuleInFile(file: File): Class<out ModuleExpansion>? {
        try {
            //模块扩展类
            val moduleClass = ClassUtil.findClass(file, ModuleExpansion::class.java)

            //如果没有模块扩展类就报错
            if (moduleClass == null) {
                console().sendLang("Module-Loader-NotClassError", file.name)
                return null
            }

            //获取模块扩展类内声明的方法
            val moduleMethods = Arrays.stream(moduleClass.declaredMethods).map { method: Method ->
                MethodSignature(
                    method.name, method.parameterTypes
                )
            }.collect(
                Collectors.toSet()
            )

            //检测有没有必须声明的方法
            if (!moduleMethods.containsAll(ABSTRACT_MODULE_METHODS)) {
                console().sendLang("Module-Loader-NotRequiredMethodError", file.name)
                return null
            }
            return moduleClass

        } catch (ex: VerifyError) {
            console().sendLang(
                "Module-Loader-VerifyError",
                file.name,
                ex.javaClass.simpleName,
                ex.message ?: if (Language.default == "zh_CN") "未知" else "UNKNOWN"
            )
            return null
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return null
    }


    private fun register(clazz: Class<out ModuleExpansion?>): Optional<ModuleExpansion> {
        try {
            val module: ModuleExpansion = createModuleInstance(clazz) ?: return Optional.empty()
            //需要模块的标识符不是空,否则就报错
            Objects.requireNonNull(module.name)
            //如果模块没注册
            return if (!module.register()) {
                Optional.empty()
            } else Optional.of(module)
        } catch (ex: LinkageError) {
            console().sendLang("Module-Loader-NotDependencyError", clazz.simpleName)
        } catch (ex: NullPointerException) {
            console().sendLang("Module-Loader-NullIdentifierError", clazz.simpleName)
        }
        return Optional.empty()
    }

    /**
     * 用模块标识符来获取模块类
     */
    private fun getExpansion(identifier: String): ModuleExpansion? {
        modulesLock.lock()
        return try {
            modules[identifier.lowercase(Locale.ROOT)]
        } finally {
            modulesLock.unlock()
        }
    }

    @ApiStatus.Internal
    fun register(module: ModuleExpansion): Boolean {
        //获取模块标识符
        val identifier = module.name.lowercase()

        val removed: ModuleExpansion? = getExpansion(identifier)
        if (removed != null && !removed.unregister()) {
            return false
        }
        modulesLock.lock()
        try {
            modules[identifier] = module
        } finally {
            modulesLock.unlock()
        }
        console().sendLang("Module-Loader-Success", module.name, module.version)
        return true
    }

    @Throws(LinkageError::class)
    fun createModuleInstance(clazz: Class<out ModuleExpansion?>): ModuleExpansion? {
        return try {
            clazz.getDeclaredConstructor().newInstance()
        } catch (ex: java.lang.Exception) {
            if (ex.cause is LinkageError) {
                throw (ex.cause as LinkageError?)!!
            }
            console().sendLang("Module-Loader-UnknownError")
            null
        }
    }

    @ApiStatus.Internal
    fun unregister(module: ModuleExpansion): Boolean {
        if (modules.remove(module.name) == null) {
            return false
        }
        return true
    }


}
