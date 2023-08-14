package cn.fd.utilities.core.util


fun Class<*>.strValue(): String {
    return this.name + '{' + this.declaredFields.map { it.name + '=' + this.getDeclaredField(it.name).toString() } + '}'
}