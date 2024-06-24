@file:Suppress("NOTHING_TO_INLINE")

package cn.fd.ratziel.core.util

inline fun <K, V> MutableMap<K, V>.putNonNull(key: K, value: V?): MutableMap<K, V> = this.apply {
    if (value != null) this[key] = value
}