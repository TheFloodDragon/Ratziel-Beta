package cn.fd.ratziel.compat

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

    /**
     * key - 优先级
     * value - 同一优先级下的 [ClassLoaderProvider]
     */
    val providers: NavigableMap<Byte, MutableList<ClassLoaderProvider>> = Collections.synchronizedNavigableMap(TreeMap())

    /**
     * 添加 [ClassLoaderProvider]
     * @param provider 类加载器提供者 [ClassLoaderProvider]
     * @param priority [provider] 的优先级
     */
    fun addProvider(provider: ClassLoaderProvider, priority: Byte = 0) = providers.computeIfAbsent(priority) { mutableListOf() }.add(provider)

    /**
     * 删除 [ClassLoaderProvider]
     */
    fun removeProvider(provider: ClassLoaderProvider) = providers.values.forEach { it.remove(provider) }

    override fun loadClass(name: String) = loadClass(name, false)

    public override fun loadClass(name: String, resolve: Boolean): Class<*> = synchronized(getClassLoadingLock(name)) {
        // 自身寻找加载过的类
        var c = findLoadedClass(name)
        // 未被加载过
        if (c == null) {
            // 尝试通过自身加载类
            c = kotlin.runCatching { findClass(name) }.getOrNull()
            // 若自身无法加载该类, 则进行进一步推断
            if (c == null) {
                // 尝试让父类加载器进行加载
                c = parent.loadClassOrNull(name)
                // 不能被加载时继续下一步。。。
                if (c == null) {
                    // 优先级遍历 [providers]
                    providers.forEach { entry ->
                        for (loader in entry.value) {
                            // 由[ClassLoaderProvider], 提供全限类定名([name])以使它动态智能分配 [ClassLoader]
                            c = loader.apply(name)?.loadClassOrNull(name)
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

}