package cn.fd.ratziel.core.util

import java.util.jar.JarEntry
import java.util.jar.JarFile

/**
 * 在Jar文件中查找
 */
fun findInJar(jar: JarFile, filter: (JarEntry) -> Boolean) = jar.entries().asSequence().filter(filter)
