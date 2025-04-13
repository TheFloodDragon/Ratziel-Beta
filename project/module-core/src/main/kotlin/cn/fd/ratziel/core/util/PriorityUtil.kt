@file:Suppress("NOTHING_TO_INLINE")

package cn.fd.ratziel.core.util

import cn.fd.ratziel.core.Priority

inline infix fun <T : Any> T.priority(priority: Byte) = Priority(priority, this)

inline fun <T : Any> T.priority() = Priority(0, this)

inline fun <T> Array<Priority<T>>.sortPriority(): List<T> = this.apply { sortBy { it.priority } }.map { it.value }

inline fun <T> Iterable<Priority<T>>.sortPriority(): List<T> = sortedBy { it.priority }.map { it.value }