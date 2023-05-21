package cn.fd.utilities.core.module

import cn.fd.utilities.common.util.toSimple
import cn.fd.utilities.core.clsload.ClassUtil.getSubClasses
import taboolib.common.platform.function.console
import taboolib.common.platform.function.getDataFolder
import taboolib.module.lang.sendLang
import java.io.File

object ModuleLoader {


    private var moduleManager = ModuleManager(listOf(File(getDataFolder().path, "workspace")))

    /**
     * 从文件夹内寻找所有文件
     */
    fun findModulesInDirs(dirs: List<File>): List<Class<out ModuleExpansion>> {/*
          获取模块文件下所有JAR后缀的文件
          如果一个文件都没有，就返回一个空的列表
         */
        val files: List<File> = arrayListOf<File>().also { list ->
            dirs.forEach { ws ->
                ws.listFiles { _, name: String ->
                    name.endsWith(".jar") || name.endsWith(".class")
                }?.forEach { list.add(it) }
            }
        }

        return if (files.isEmpty()) emptyList()
        else {
            //返回所有文件内模块扩展类的集合
            files.map { file: File? ->
                findModuleClass(file!!)
            }.toSimple()
        }
    }


    //注册模块
    fun ModuleExpansion.register(): Boolean {
        //获取模块标识符
        val identifier = this.name

//        val removed: ModuleExpansion? = ModuleManager.getModule(identifier)
//        if (removed != null && !removed.unregister()) {
//            return false
//        }

        moduleManager.modules[ModuleInfo(identifier)] = this

        console().sendLang("Module-Loader-Success", identifier, this.version)
        return true
    }

    //卸载模块
    fun ModuleExpansion.unregister() {
        moduleManager.modules.remove(ModuleInfo(this.name))
        console().sendLang("Module-Loader-Unregistered", this.name, this.version)
    }

    /**
     * 从单个文件中寻找模块扩展类
     * @param file 要被寻找的单个文件
     */
    fun findModuleClass(file: File): List<Class<out ModuleExpansion>> {
        try {
            //获取继承的子类 (先加载,后获取子类)
            val subClasses = getSubClasses(file, ModuleExpansion::class.java)
            //如果是JAR文件且找不到模块扩展类
            if (file.endsWith(".jar") && subClasses.isEmpty()) {
                console().sendLang("Module-Loader-NotClassError", file.name)
                return listOf()
            }
            //::Begin 似乎没什么用
            //获取模块扩展类内声明的方法
//            val moduleMethods = Arrays.stream(mClass!!.declaredMethods).map { method: Method ->
//                MethodSignature(method.name, method.parameterTypes)
//            }.collect(Collectors.toSet())
//            //检测有没有必须声明的方法
//            if (!moduleMethods.containsAll(
//                    Arrays.stream(ModuleExpansion::class.java.declaredMethods).filter { method: Method ->
//                        Modifier.isAbstract(method.modifiers)
//                    }.map { method: Method ->
//                        MethodSignature(method.name, method.parameterTypes)
//                    }.collect(Collectors.toSet())
//                )
//            ) {
//                console().sendLang("Module-Loader-NotRequiredMethodError", file.name)
//                return null
//            }
            //::END

            return subClasses

        } catch (ex: VerifyError) {
            console().sendLang("Module-Loader-VerifyError", file.name, ex.javaClass.simpleName, ex.message ?: "UNKNOWN")
            return listOf()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return listOf()
    }

    fun setModuleManager(mm: ModuleManager) {
        moduleManager = mm
    }

    fun getModuleManager(): ModuleManager {
        return moduleManager
    }

}