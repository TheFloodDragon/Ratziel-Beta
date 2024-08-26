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
        is Iterable<*> -> NBTList.of(target.mapNotNull { e -> e?.let { tryUnbox(it) } })
        is Array<*> -> NBTList.of(target.mapNotNull { e -> e?.let { tryUnbox(it) } })
        is Map<*, *> -> boxMap(target)
        else -> null
    } ?: throw UnsupportedTypeException(target)

    @JvmStatic
    fun unbox(target: NBTData): Any = when (target) {
        is NBTCompound -> unboxMap(target)
        is NBTList -> target.map { unbox(it) }
        else -> target.content
    }

    @JvmStatic
    fun tryUnbox(target: Any): Any = if (target is NBTData) unbox(target) else target

    @JvmStatic
    fun boxMap(target: Map<*, *>): NBTCompound = NBTCompound().apply {
        for ((key, value) in target) {
            sourceMap[(key ?: continue).toString()] = tryUnbox(value ?: continue)
        }
    }

    @JvmStatic
    fun unboxMap(target: NBTCompound): Map<String, Any> = target.sourceMap

}