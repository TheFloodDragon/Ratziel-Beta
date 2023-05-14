package cn.fd.utilities.util

import java.io.File
import java.io.IOException
import java.net.URLClassLoader
import java.util.*
import java.util.jar.JarEntry
import java.util.jar.JarFile

object ClassUtil {

    @Throws(IOException::class, ClassNotFoundException::class)
    fun <T> findClass(file: File, clazz: Class<T>): Class<out T>? {
        //检查文件是否存在
        if (!file.exists()) {
            return null
        }

        val jar = file.toURI().toURL()
        val loader = URLClassLoader(arrayOf(jar), clazz.classLoader)
        val classes: MutableList<Class<out T>> = ArrayList()

        val entries: MutableList<JarEntry> = arrayListOf()

        JarFile(file).entries().let { e ->
            while (e.hasMoreElements()) {
                e.nextElement().let {
                    if (it.name.endsWith(".class")) entries.add(it)
                }
            }
        }

        entries.forEach {
            val className = it.name.replace('/', '.').substring(0, it.name.length - 6)
            try {
                //val loaded = loadClass(name)
                val loaded = loader.loadClass(className)
                if (clazz.isAssignableFrom(loaded)) {
                    classes.add(loaded.asSubclass(clazz))
                }
            } catch (ignored: NoClassDefFoundError) {
            }
        }

        if (classes.isEmpty()) {
            return null
        }
        return classes[0]
    }


    /**
     * 加载类
     */
    @Throws(ClassNotFoundException::class)
    fun loadClass(className: String, isInitialized: Boolean = false): Class<*> {
        return Class.forName(className, isInitialized, Thread.currentThread().contextClassLoader)
    }


}
