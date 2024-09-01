package cn.fd.ratziel.module.nbt

import cn.fd.ratziel.core.exception.UnsupportedTypeException

/**
 * NBTAdapter
 *
 * @author TheFloodDragon
 * @since 2024/3/15 20:13
 */
object NBTAdapter {

    @JvmStatic
    fun box(target: Any): NBTData = when (target) {
        is NBTData -> target
        is String -> NBTString(target)
        is Int -> NBTInt(target)
        is Double -> NBTDouble(target)
        is Boolean -> NBTByte(target)
        is Byte -> NBTByte(target)
        is Float -> NBTFloat(target)
        is Long -> NBTLong(target)
        is Short -> NBTShort(target)
        is IntArray -> NBTIntArray(target)
        is ByteArray -> NBTByteArray(target)
        is LongArray -> NBTLongArray(target)
        is Iterable<*> -> boxList(target)
        is Array<*> -> boxList(target)
        is Map<*, *> -> boxMap(target)
        else -> null
    } ?: throw UnsupportedTypeException(target)

    @JvmStatic
    fun boxMap(target: Map<*, *>): NBTCompound = NBTCompound().apply {
        for ((key, value) in target) {
            sourceMap[(key ?: continue).toString()] = box(value ?: continue)
        }
    }

    @JvmStatic
    fun boxList(list: Iterable<*>): NBTList = NBTList.of(list.mapNotNull { it?.let(::box) })

    @JvmStatic
    fun boxList(list: Array<*>): NBTList = NBTList.of(list.mapNotNull { it?.let(::box) })

    @JvmStatic
    fun unbox(target: NBTData): Any = when (target) {
        is NBTCompound -> unboxMap(target)
        is NBTList -> target.map { unbox(it) }
        else -> target.content
    }

    @JvmStatic
    fun unboxMap(target: NBTCompound): Map<String, Any> = target.content.mapValues { unbox(it.value) }

    @JvmStatic
    fun tryUnbox(target: Any): Any = if (target is NBTData) unbox(target) else target

}