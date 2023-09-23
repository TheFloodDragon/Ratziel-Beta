package cn.fd.ratziel.core.util

/**
 * 查找类是否存在
 * @return 是否能成功找到该类
 */
fun hasClass(clazz: String) =
    try {
        Class.forName(clazz); true
    } catch (e: ClassNotFoundException) { false }

fun hasClass(clazz: String, initialize: Boolean = false, loader: ClassLoader) =
    try {
        Class.forName(clazz, initialize, loader); true
    } catch (e: ClassNotFoundException) { false }