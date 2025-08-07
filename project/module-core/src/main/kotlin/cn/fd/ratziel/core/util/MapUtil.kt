package cn.fd.ratziel.core.util

fun <K, V> Map<K, V>.getBy(names: Iterable<K>): V? {
    for (name in names) {
        val find = this[name]
        if (find != null) return find
    }
    return null
}

fun <K, V> Map<K, V>.getBy(vararg names: K): V? {
    for (name in names) {
        val find = this[name]
        if (find != null) return find
    }
    return null
}
