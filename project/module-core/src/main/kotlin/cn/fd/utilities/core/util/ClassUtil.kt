package cn.fd.utilities.core.util

/**
 * 类通用toString方法
 */
fun Class<*>.strValue(): String {
    return this.name + '{' +
            this.declaredFields.map {
                it.name + '=' +
                        this.getDeclaredField(it.name).apply { setAccessible(true) }
                            .get(this).toString()
            } + '}'
}