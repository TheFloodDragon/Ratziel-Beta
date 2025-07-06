package cn.fd.ratziel.core.util

import java.io.File
import java.io.InputStream
import java.util.function.Function
import java.util.jar.JarEntry
import java.util.jar.JarFile

/**
 * JarUtil
 *
 * @author TheFloodDragon
 * @since 2025/7/6 15:40
 */
object JarUtil {

    /**
     * 在Jar文件中查找
     */
    @JvmStatic
    fun findInJar(jar: JarFile, filter: (JarEntry) -> Boolean) = jar.entries().asSequence().filter(filter)

    /**
     * 获取Jar文件中的所有条目
     *
     * @param filter 条目过滤器
     * @return 符合条件的条目输入流列表
     */
    @JvmStatic
    fun getEntries(jarFile: File, filter: Function<JarEntry, Boolean>): List<InputStream> = buildList {
        val jar = JarFile(jarFile)
        findInJar(jar) {
            !it.isDirectory && filter.apply(it)
        }.forEach { jarEntry ->
            add(jar.getInputStream(jarEntry))
        }
    }

    /**
     * 获取Jar文件中的所有条目
     *
     * @param filter 条目过滤器
     * @return 符合条件的条目输入流列表
     */
    @JvmStatic
    fun getEntries(filter: Function<JarEntry, Boolean>) = getEntries(taboolib.common.platform.function.getJarFile(), filter)

}
