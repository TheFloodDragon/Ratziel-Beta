package cn.fd.ratziel.core.util

/**
 * 双重遍历
 */
fun <T> Iterable<Iterable<T>>.forEachDually(action: (T) -> Unit) {
    for (parent in this) {
        for (child in parent) {
            action(child)
        }
    }
}