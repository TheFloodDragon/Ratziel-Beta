package cn.fd.utilities.clsload

import taboolib.common5.util.getStackTraceString
import java.io.File
import java.net.URLClassLoader
import java.util.jar.JarEntry
import java.util.jar.JarFile

object ClassUtil {

    fun loadClasses(
        file: File, /*获取所有包名*/
        classLoader: ClassLoader
    ): List<Class<*>> {
        //如果文件不存在
        if (!file.exists()) return listOf()

        val classes: MutableList<Class<*>> = mutableListOf()
        //如果是jar文件
        if (file.extension == "jar") {
            getClassesName(file).forEach {
                //加载类(不初始化)
                try {
                    val loader = URLClassLoader(arrayOf(file.toURI().toURL()), classLoader)
                    //classes.add(loadClass(it, initialize))
                    classes.add(loader.loadClass(it))
                } catch (ex: Exception) {
                    println("发现错误: ${ex.getStackTraceString()}")
                }
            }
        }
        //如果是class文件
        else if (file.extension == "class") {
        }//TODO

        return classes
    }

    fun getClassesName(file: File): List<String> {
        val cns: MutableList<String> = mutableListOf()

        val entries: MutableList<JarEntry> = mutableListOf()

        JarFile(file).entries().let { e ->
            while (e.hasMoreElements()) {
                e.nextElement().let {
                    if (it.name.endsWith(".class")) entries.add(it)
                }
            }
        }

        entries.forEach {
            //采用一般文件名替换的方式获取class包名
            cns.add(it.name.toPackageName())
        }

        return cns
    }


    fun <T> List<Class<*>>.findSubClass(file: File, clazz: Class<T>): List<Class<out T>> {
        //检查文件是否存在
        if (!file.exists()) return listOf()

        val classes: MutableList<Class<out T>> = mutableListOf()

        //加载所有类(不初始化)
        this.forEach {
            if (clazz.isAssignableFrom(it)) {
                classes.add(it.asSubclass(clazz))
            }
        }

        return classes
    }

    private fun String.toPackageName(): String {
        return this.replace('/', '.').substring(0, this.length - 6)
    }


}
