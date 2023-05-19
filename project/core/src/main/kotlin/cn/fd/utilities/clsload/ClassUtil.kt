package cn.fd.utilities.clsload

import cn.fd.utilities.clsload.FClassLoader.loadClass
import java.io.File
import java.util.jar.JarEntry
import java.util.jar.JarFile

object ClassUtil {

    fun loadClasses(
        file: File, /*获取所有包名*/
        pkgNames: List<String> = getClasses(file),
        initialize: Boolean = false
    ): List<Class<*>> {
        if (!file.exists()) return listOf()
        return mutableListOf<Class<*>>().apply {
            pkgNames.forEach {
                //加载类(不初始化)
                add(loadClass(it, initialize))
            }
        }
    }

    fun getClasses(file: File): List<String> {
        if (!file.exists()) return listOf()

        val entries: MutableList<JarEntry> = arrayListOf()

        JarFile(file).entries().let { e ->
            while (e.hasMoreElements()) {
                e.nextElement().let {
                    if (it.name.endsWith(".class")) entries.add(it)
                }
            }
        }

        return mutableListOf<String>().apply {
            entries.forEach {
                //采用一般文件名替换的方式获取class包名
                add(it.name.replace('/', '.').substring(0, it.name.length - 6))
            }
        }
    }


    fun <T> List<Class<*>>.findSubClass(file: File, clazz: Class<T>): List<Class<out T>> {
        //检查文件是否存在
        if (!file.exists()) return listOf()

        val classes: List<Class<*>> = this

        return mutableListOf<Class<out T>>().apply {
            //加载所有类(不初始化)
            classes.forEach {
                if (clazz.isAssignableFrom(it)) {
                    add(it.asSubclass(clazz))
                }
            }
        }
    }


}
