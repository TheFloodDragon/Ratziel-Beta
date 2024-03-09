package cn.fd.ratziel.compat

import cn.fd.ratziel.core.function.loadClassOrNull
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

    constructor(clazz: Class<*>, parent: ClassLoader) : this(arrayOf<URL>(clazz.protectionDomain.codeSource.location), parent)

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

    /**
     * 自主加载条件
     */
    var selfLoadCondition = java.util.function.Function<String, Boolean> { false }

    override fun loadClass(name: String) = loadClass(name, false)

    override fun loadClass(name: String, resolve: Boolean) = loadClass(name, resolve, false)

    internal fun loadClass(name: String, resolve: Boolean, forceSelfLoad: Boolean): Class<*> = synchronized(getClassLoadingLock(name)) {
        // 自身寻找加载过的类
        var c = findLoadedClass(name)
        // 未被加载过
        if (c == null) {
            // 优先加载可被自身加载的类
            if (selfLoadCondition.apply(name) || forceSelfLoad) {
                c = kotlin.runCatching { findClass(name) }.getOrNull()
            } else {
                // 尝试通过注册的ClassLoaderProvider加载 (有优先级)
                providers.forEach { entry ->
                    for (loader in entry.value) {
                        // 由[ClassLoaderProvider], 提供全限类定名([name])以使它动态智能分配 [ClassLoader]
                        c = loader.apply(name)?.loadClassOrNull(name)
                        // 直到类能被加载为止
                        if (c != null) break
                    }
                }
            }
            // 尝试让父类加载器加载, 无法时加载抛出异常
            if (c == null) c = parent.loadClassOrNull(name) ?: throw ClassNotFoundException()
            // 连接类
            if (resolve) this.resolveClass(c)
        }
        return c
    }

}