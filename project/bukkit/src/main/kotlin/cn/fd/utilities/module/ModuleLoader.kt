package cn.fd.utilities.module

import cn.fd.utilities.util.ClassUtil
import taboolib.common.platform.function.console
import taboolib.module.lang.sendLang
import java.io.File
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.util.*
import java.util.stream.Collectors

object ModuleLoader {

    /**
     * 从模块文件夹内寻找所有文件
     */
    fun findModules(): List<Class<out ModuleExpansion?>?> {

        /*
          获取模块文件下所有JAR后缀的文件
          如果一个文件都没有，就返回一个空的列表
         */
        val files: List<File> = arrayListOf<File>().also { list ->
            ModuleManager.getWorkspaces().forEach { ws ->
                ws.listFiles { _, name: String ->
                    name.endsWith(".jar")
                }?.forEach { list.add(it) }
            }
        }

        return if (files.isEmpty()) emptyList()
        else {
            //返回所有文件内模块扩展类的集合
            files.map { file: File? ->
                findModuleInFile(file!!)
            }.toList()
        }
    }

    /**
     * 从单个文件中寻找模块扩展类
     * @param file 要被寻找的单个文件
     */
    private fun findModuleInFile(file: File): Class<out ModuleExpansion>? {
        try {
            //获取继承的子类
            val mClass = ClassUtil.findClass(file, ModuleExpansion::class.java)

            //如果是JAR文件且找不到模块扩展类
            if (file.endsWith(".jar") && mClass == null) {
                console().sendLang("Module-Loader-NotClassError", file.name)
                return null
            }

            //::Begin 似乎没什么用
            //获取模块扩展类内声明的方法
            val moduleMethods = Arrays.stream(mClass!!.declaredMethods).map { method: Method ->
                MethodSignature(method.name, method.parameterTypes)
            }.collect(Collectors.toSet())

            //检测有没有必须声明的方法
            if (!moduleMethods.containsAll(
                    Arrays.stream(ModuleExpansion::class.java.declaredMethods).filter { method: Method ->
                        Modifier.isAbstract(method.modifiers)
                    }.map { method: Method ->
                        MethodSignature(method.name, method.parameterTypes)
                    }.collect(Collectors.toSet())
                )
            ) {
                console().sendLang("Module-Loader-NotRequiredMethodError", file.name)
                return null
            }
            //::END

            return mClass

        } catch (ex: VerifyError) {
            console().sendLang("Module-Loader-VerifyError", file.name, ex.javaClass.simpleName, ex.message ?: "UNKNOWN")
            return null
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return null
    }

}