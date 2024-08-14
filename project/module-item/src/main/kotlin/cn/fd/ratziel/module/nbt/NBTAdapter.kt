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
    fun adapt(target: Any): NBTData = when (target) {
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
        is Iterable<*> -> NBTList(target.mapNotNull { e -> e?.let { adapt(it) } })
        is Array<*> -> NBTList(target.mapNotNull { e -> e?.let { adapt(it) } })
        is Map<*, *> -> adaptMap(target)
        else -> null
    } ?: throw UnsupportedTypeException(target)

    @JvmStatic
    fun adaptMap(target: Map<*, *>) = NBTCompound().apply {
        for ((k, v) in target) {
            put(k?.toString() ?: continue, v?.let { adapt(it) } ?: continue)
        }
    }

}