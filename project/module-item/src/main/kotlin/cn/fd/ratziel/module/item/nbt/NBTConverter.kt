package cn.fd.ratziel.module.item.nbt

import cn.fd.ratziel.core.exception.UnsupportedTypeException

/**
 * NBTConverter
 *
 * @author TheFloodDragon
 * @since 2024/3/15 20:13
 */
object NBTConverter {

    fun convert(target: Any): NBTData = when {
        target is NBTData -> target
        isNmsNBT(target) -> NmsConverter.convert(target)
        else -> BasicConverter.convert(target)
    } ?: throw UnsupportedTypeException(target)

    /**
     * 判断目标是否为 NmsNBT
     */
    fun isNmsNBT(target: Any) = NMSUtil.NtBase.nmsClass.isAssignableFrom(target::class.java)

    object NmsConverter {

        fun convert(target: Any) = target::class.java.let { c ->
            when {
                NMSUtil.NtCompound.nmsClass.isAssignableFrom(c) -> NBTCompound(target)
                NMSUtil.NtList.nmsClass.isAssignableFrom(c) -> NBTList(target)
                NMSUtil.NtString.nmsClass.isAssignableFrom(c) -> NBTString(target)
                NMSUtil.NtInt.nmsClass.isAssignableFrom(c) -> NBTInt(target)
                NMSUtil.NtDouble.nmsClass.isAssignableFrom(c) -> NBTDouble(target)
                NMSUtil.NtByte.nmsClass.isAssignableFrom(c) -> NBTByte(target)
                NMSUtil.NtFloat.nmsClass.isAssignableFrom(c) -> NBTFloat(target)
                NMSUtil.NtLong.nmsClass.isAssignableFrom(c) -> NBTLong(target)
                NMSUtil.NtShort.nmsClass.isAssignableFrom(c) -> NBTShort(target)
                NMSUtil.NtIntArray.nmsClass.isAssignableFrom(c) -> NBTIntArray(target)
                NMSUtil.NtByteArray.nmsClass.isAssignableFrom(c) -> NBTByteArray(target)
                NMSUtil.NtLongArray.nmsClass.isAssignableFrom(c) -> NBTLongArray(target)
                else -> null
            }
        }

    }

    object BasicConverter {

        fun convert(target: Any): NBTData? = when (target) {
            // 基本类型转换
            is String -> NBTString(NBTString.new(target))
            is Int -> NBTInt(NBTInt.new(target))
            is Double -> NBTDouble(NBTDouble.new(target))
            is Boolean -> NBTByte(NBTByte.new(target))
            is Byte -> NBTByte(NBTByte.new(target))
            is Float -> NBTFloat(NBTFloat.new(target))
            is Long -> NBTLong(NBTLong.new(target))
            is Short -> NBTShort(NBTShort.new(target))
            is IntArray -> NBTIntArray(NBTIntArray.new(target))
            is ByteArray -> NBTByteArray(NBTByteArray.new(target))
            is LongArray -> NBTLongArray(NBTLongArray.new(target))
            is Iterable<*> -> convertList(target)
            is Array<*> -> convertList(listOf(target))
            is Map<*, *> -> convertMap(target)
            else -> null
        }

        fun convertList(source: Iterable<*>): NBTList = ArrayList<Any>().apply {
            source.forEach { add(NBTConverter.convert(it!!).getData()) }
        }.let { NBTList(NBTList.new(it)) }

        fun convertMap(source: Map<*, *>): NBTCompound = HashMap<String, Any>().apply {
            source.forEach { put(it.key.toString(), NBTConverter.convert(it.value!!).getData()) }
        }.let { NBTCompound(NBTCompound.new(it)) }

    }

}