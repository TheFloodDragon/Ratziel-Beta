package cn.fd.ratziel.compat.inject

import cn.fd.ratziel.core.function.loadClassOrNull
import taboolib.common.classloader.IsolatedClassLoader

/**
 * IntrusiveClassLoader
 *
 * @author TheFloodDragon
 * @since 2024/3/22 21:27
 */
class IntrusiveClassLoader(parent: ClassLoader) : ClassLoader(parent) {

    override fun loadClass(name: String?) = loadClass(name, false)

    override fun loadClass(name: String?, resolve: Boolean): Class<*> {
        // 优先父级加载
        var find = parent.loadClassOrNull(name)
        // 隔离类加载器加载 (不检查其父级)
        if (find == null) find = try {
            if (name?.startsWith("cn.fd.ratziel") == true)
                IsolatedClassLoader.INSTANCE.loadClass(name, resolve, false)
            else null
        } catch (_: ClassNotFoundException) {
            null
        }
        // 返回值
        return find ?: throw ClassNotFoundException()
    }

}