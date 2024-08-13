package cn.fd.ratziel.module.item.nbt

import cn.fd.ratziel.core.exception.UnsupportedTypeException

/**
 * NBTAdapter
 *
 * @author TheFloodDragon
 * @since 2024/3/15 20:13
 */
object NBTAdapter {

    @JvmStatic
    fun adapt(target: Any): NBTData = when {
        target is NBTData -> target
        isNmsNBT(target) -> NmsAdapter.adapt(target)
        else -> BasicAdapter.adapt(target)
    } ?: throw UnsupportedTypeException(target)

    @JvmStatic
    fun adaptNms(target: Any) = NmsAdapter.adapt(target)!!

    @JvmStatic
    fun adaptList(target: Iterable<*>): NBTList = ArrayList<Any>().apply {
        target.forEach { unsure -> unsure?.let { add(adapt(it).getRaw()) } }
    }.let { NBTList.of(NBTList.new(it)) }

    @JvmStatic
    fun adaptMap(target: Map<*, *>): NBTCompound = HashMap<String, Any>().apply {
        target.forEach { (node, unsure) -> unsure?.let { put(node.toString(), adapt(it).getRaw()) } }
    }.let { NBTCompound.of(NBTCompound.new(it)) }

    /**
     * 判断目标是否为 NmsNBT
     */
    @JvmStatic
    fun isNmsNBT(target: Any) = NMSUtil.NtBase.nmsClass.isAssignableFrom(target::class.java)

    object NmsAdapter {

        @JvmStatic
        fun adapt(target: Any) = target::class.java.let { c ->
            when {
                NMSUtil.NtCompound.nmsClass.isAssignableFrom(c) -> NBTCompound.of(target)
                NMSUtil.NtList.nmsClass.isAssignableFrom(c) -> NBTList.of(target)
                NMSUtil.NtString.nmsClass.isAssignableFrom(c) -> NBTString.of(target)
                NMSUtil.NtInt.nmsClass.isAssignableFrom(c) -> NBTInt.of(target)
                NMSUtil.NtDouble.nmsClass.isAssignableFrom(c) -> NBTDouble.of(target)
                NMSUtil.NtByte.nmsClass.isAssignableFrom(c) -> NBTByte.of(target)
                NMSUtil.NtFloat.nmsClass.isAssignableFrom(c) -> NBTFloat.of(target)
                NMSUtil.NtLong.nmsClass.isAssignableFrom(c) -> NBTLong.of(target)
                NMSUtil.NtShort.nmsClass.isAssignableFrom(c) -> NBTShort.of(target)
                NMSUtil.NtIntArray.nmsClass.isAssignableFrom(c) -> NBTIntArray.of(target)
                NMSUtil.NtByteArray.nmsClass.isAssignableFrom(c) -> NBTByteArray.of(target)
                NMSUtil.NtLongArray.nmsClass.isAssignableFrom(c) -> NBTLongArray.of(target)
                else -> null
            }
        }

    }

    object BasicAdapter {

        @JvmStatic
        fun adapt(target: Any): NBTData? = when (target) {
            // 基本类型转换
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
            is Iterable<*> -> adaptList(target)
            is Array<*> -> adaptList(listOf(target))
            is Map<*, *> -> adaptMap(target)
            else -> null
        }

    }

}