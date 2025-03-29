package cn.fd.ratziel.module.nbt

import cn.altawk.nbt.tag.*
import cn.fd.ratziel.core.exception.UnsupportedTypeException

/**
 * NbtAdapter
 *
 * @author TheFloodDragon
 * @since 2024/3/15 20:13
 */
object NbtAdapter {

    @JvmStatic
    fun box(target: Any): NbtTag = when (target) {
        is NbtTag -> target
        is String -> NbtString(target)
        is Int -> NbtInt(target)
        is Double -> NbtDouble(target)
        is Boolean -> NbtByte(target)
        is Byte -> NbtByte(target)
        is Float -> NbtFloat(target)
        is Long -> NbtLong(target)
        is Short -> NbtShort(target)
        is IntArray -> NbtIntArray(target)
        is ByteArray -> NbtByteArray(target)
        is LongArray -> NbtLongArray(target)
        is Iterable<*> -> boxList(target)
        is Array<*> -> boxList(target)
        is Map<*, *> -> boxMap(target)
        else -> null
    } ?: throw UnsupportedTypeException(target)

    @JvmStatic
    fun boxMap(target: Map<*, *>): NbtCompound = NbtCompound().apply {
        for ((key, value) in target) {
            content[(key ?: continue).toString()] = box(value ?: continue)
        }
    }

    @JvmStatic
    fun boxList(list: Iterable<*>): NbtList = NbtList.of(list.mapNotNull { it?.let(::box) })

    @JvmStatic
    fun boxList(list: Array<*>): NbtList = NbtList.of(list.mapNotNull { it?.let(::box) })

    @JvmStatic
    fun unbox(target: NbtTag): Any = when (target) {
        is NbtCompound -> unboxMap(target)
        is NbtList -> target.map { unbox(it) }
        else -> target.content
    }

    @JvmStatic
    fun unboxMap(target: NbtCompound): Map<String, Any> = target.content.mapValues { unbox(it.value) }

    @JvmStatic
    fun tryUnbox(target: Any): Any = if (target is NbtTag) unbox(target) else target

}
