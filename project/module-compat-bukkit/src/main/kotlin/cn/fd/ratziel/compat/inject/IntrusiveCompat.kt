package cn.fd.ratziel.compat.inject

import taboolib.common.PrimitiveLoader
import taboolib.common.classloader.IsolatedClassLoader
import taboolib.library.reflex.UnsafeAccess

/**
 * IntrusiveCompat
 *
 * @author TheFloodDragon
 * @since 2024/3/22 21:25
 */
object IntrusiveCompat {

    private val pluginClassLoader get() = IsolatedClassLoader.INSTANCE.parent

    private val globalClassLoader = pluginClassLoader.parent

    private val setter = UnsafeAccess.lookup.findSetter(ClassLoader::class.java, "parent", ClassLoader::class.java)

//    @Awake
    fun inject() {
        println(pluginClassLoader.parent)
        setter.bindTo(pluginClassLoader).invokeWithArguments(IntrusiveClassLoader(globalClassLoader))
        println(pluginClassLoader.parent)
        // Test
        println(PrimitiveLoader.TABOOLIB_GROUP)
    }

}