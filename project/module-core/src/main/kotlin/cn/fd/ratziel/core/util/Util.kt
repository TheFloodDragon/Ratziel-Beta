package cn.fd.ratziel.core.util

import java.util.*

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

fun randomUUID() = UUID.randomUUID().toString().replace("-", "").lowercase()