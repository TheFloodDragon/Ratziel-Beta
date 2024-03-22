package cn.fd.ratziel.compat.inject

import taboolib.common.PrimitiveLoader
import taboolib.common.classloader.IsolatedClassLoader
import taboolib.common.platform.Awake
import taboolib.library.reflex.ReflexClass

/**
 * IntrusiveCompat
 *
 * @author TheFloodDragon
 * @since 2024/3/22 21:25
 */
object IntrusiveCompat {

    private val pluginClassLoader = IsolatedClassLoader.INSTANCE.parent

    private val globalClassLoader = pluginClassLoader.parent

    private val parentField by lazy { ReflexClass.of(pluginClassLoader::class.java, false).getField("parent", remap = false) }

    @Awake
    fun inject() {
        parentField.set(parentField, IntrusiveClassLoader(globalClassLoader))
        println(PrimitiveLoader.TABOOLIB_GROUP)
    }

}