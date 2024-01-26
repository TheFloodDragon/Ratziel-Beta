package cn.fd.ratziel.core.util

import taboolib.common.io.digest
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

/**
 * 加密 (默认SHA-256)
 */
fun String.digest() = digest(DEFAULT_ALGORITHM)

const val DEFAULT_ALGORITHM = "SHA-256"

fun randomUUID() = UUID.randomUUID().toString().replace("-", "").lowercase()