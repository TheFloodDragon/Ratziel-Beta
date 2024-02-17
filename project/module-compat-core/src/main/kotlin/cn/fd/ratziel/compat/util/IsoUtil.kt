package cn.fd.ratziel.compat.util

import taboolib.library.reflex.Reflex.Companion.getProperty

private const val INSTANCE_FIELD_NAME = "INSTANCE"

/**
 * 通过全限类定名和类加载器
 * 获取 [IsolatedClassLoader] 实例
 */
fun isoInstance(path: String, classLoader: ClassLoader) = classLoader.loadClass(path).getProperty<ClassLoader>(INSTANCE_FIELD_NAME, isStatic = true)