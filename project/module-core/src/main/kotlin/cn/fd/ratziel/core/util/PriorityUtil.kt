@file:Suppress("NOTHING_TO_INLINE")

package cn.fd.ratziel.core.util

import cn.fd.ratziel.core.Prioritized

inline infix fun <T : Any> T.priority(priority: Byte) = Prioritized(priority, this)

inline fun <T : Any> T.priority() = Prioritized(0, this)

inline fun <T> Array<Prioritized<T>>.sortPriority(): List<T> = this.apply { sortBy { it.priority } }.map { it.value }

inline fun <T> Iterable<Prioritized<T>>.sortPriority(): List<T> = sortedBy { it.priority }.map { it.value }