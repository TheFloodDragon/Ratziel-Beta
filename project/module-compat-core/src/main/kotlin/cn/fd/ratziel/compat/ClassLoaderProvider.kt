package cn.fd.ratziel.compat

import java.util.function.Function

/**
 * ClassLoaderProvider - 通过全限类定名提供类加载器
 *
 * @author TheFloodDragon
 * @since 2024/2/17 13:07
 */
class ClassLoaderProvider(
    val function: Function<String, ClassLoader>
) : Function<String, ClassLoader> {

    override fun apply(t: String) = function.apply(t)

}