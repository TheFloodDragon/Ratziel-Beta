package cn.fd.ratziel.compat

import taboolib.common.classloader.IsolatedClassLoader
import java.net.URL
import java.net.URLClassLoader
import java.util.*


/**
 * CompatibleClassLoader
 *
 * @author TheFloodDragon
 * @since 2024/2/15 15:25
 */
class CompatibleClassLoader(urls: Array<URL>, parent: ClassLoader) : URLClassLoader(urls, parent) {

    constructor() : this(CompatibleClassLoader::class.java)

    constructor(clazz: Class<*>) : this(arrayOf<URL>(clazz.protectionDomain.codeSource.location), clazz.getClassLoader())

    val hookedLoaderMap: NavigableMap<Byte, MutableList<ClassLoader>> = Collections.synchronizedNavigableMap(TreeMap())

    override fun loadClass(name: String) = loadClass(name, false)

    override fun loadClass(name: String?, resolve: Boolean) = loadClass(name, resolve, null)

    fun loadClass(name: String?, resolve: Boolean, precedence: ClassLoader?): Class<*> = synchronized(getClassLoadingLock(name)) {
        // 自身寻找加载过的类
        var c = findLoadedClass(name)
        // 未被加载过
        if (c == null) {
            // 尝试通过自身加载类
            c = kotlin.runCatching { findClass(name) }.getOrNull()
            // 若自身无法加载该类, 则进行进一步推断
            if (c == null) {
                // 尝试让父类加载器和优先者先进行加载
                c = parent.loadClassOrNull(name)
                    ?: precedence?.loadClassOrNull(name)
                // 不能被加载时继续下一步。。。
                if (c == null) {
                    // 反则不能被优先者加载的
                    hookedLoaderMap.forEach { entry ->
                        for (loader in entry.value) {
                            // 交给挂钩的 ClassLoader 加载
                            c = loader.loadClassOrNull(name)
                            // 直到类能被加载为止
                            if (c != null) break
                        }
                    }
                }
            }
            // 连接类
            if (resolve) this.resolveClass(c)
        }
        return c ?: throw ClassNotFoundException()
    }

    fun ClassLoader.loadClassOrNull(name: String?): Class<*>? =
        kotlin.runCatching { loadClass(name) }.getOrNull()

    companion object {

        /**
         * 默认实例
         * [parent] 为 [IsolatedClassLoader]
         */
        val instance by lazy { CompatibleClassLoader(IsolatedClassLoader::class.java) }

    }

}