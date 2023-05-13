package cn.fd.fdutilities.util

import java.io.File
import java.io.IOException
import java.net.URLClassLoader
import java.util.jar.JarEntry
import java.util.jar.JarInputStream


@Throws(IOException::class, ClassNotFoundException::class)
fun <T> findClass(file: File, clazz: Class<T>): Class<out T>? {
    //检查文件是否存在
    if (!file.exists()) {
        return null
    }

    val jar = file.toURI().toURL()
    val loader = URLClassLoader(arrayOf(jar), clazz.classLoader)
    val matches: MutableList<String> = ArrayList()
    val classes: MutableList<Class<out T>> = ArrayList()
    JarInputStream(jar.openStream()).use { stream ->
        var entry: JarEntry
        while (stream.nextJarEntry.also { entry = it } != null) {
            val name = entry.name
            if (name.isEmpty() || !name.endsWith(".class")) {
                continue
            }
            matches.add(name.substring(0, name.lastIndexOf('.')).replace('/', '.'))
        }
        for (match in matches) {
            try {
                val loaded = loader.loadClass(match)
                if (clazz.isAssignableFrom(loaded)) {
                    classes.add(loaded.asSubclass(clazz))
                }
            } catch (ignored: NoClassDefFoundError) {
            }
        }
    }
    if (classes.isEmpty()) {
        loader.close()
        return null
    }
    return classes[0]
}
