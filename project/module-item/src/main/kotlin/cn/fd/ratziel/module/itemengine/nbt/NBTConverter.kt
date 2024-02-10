package cn.fd.ratziel.module.itemengine.nbt

import cn.fd.ratziel.core.exception.UnsupportedTypeException
import cn.fd.ratziel.core.function.isAssignableTo

/**
 * NBTConverter - NBT转换器
 * 可将 TiNBT 和 NmsNBT 转换成 NBTData
 *
 * @author TheFloodDragon
 * @since 2023/12/2 21:36
 */
object NBTConverter {

    fun convert(obj: Any): NBTData = when {
        obj is NBTData -> obj
        obj is TiNBTData -> TiConverter.convert(obj)
        checkIsNmsNBT(obj) -> NmsConverter.convert(obj)
        else -> BasicConverter.convert(obj)
    } ?: throw UnsupportedTypeException(obj)


    object BasicConverter : Converter<Any> {
        override fun convert(obj: Any) = when (obj) {
            // 基本类型转换
            is String -> NBTString(obj)
            is Int -> NBTInt(obj)
            is Double -> NBTDouble(obj)
            is Boolean -> NBTBoolean(obj)
            is Byte -> NBTByte(obj)
            is Float -> NBTFloat(obj)
            is Long -> NBTLong(obj)
            is Short -> NBTShort(obj)
            is IntArray -> NBTIntArray(obj)
            is ByteArray -> NBTByteArray(obj)
            is LongArray -> NBTLongArray(obj)
            is Array<*>, is Iterable<*> -> NBTList(obj)
            else -> null
        }
    }

    object NmsConverter : Converter<Any> {
        override fun convert(obj: Any) = obj::class.java.run {
            when {
                isAssignableTo(NBTCompound.clazz) -> NBTCompound(obj)
                isAssignableTo(NBTList.clazz) -> NBTList(obj)
                isAssignableTo(NBTString.clazz) -> NBTString(obj)
                isAssignableTo(NBTInt.clazz) -> NBTInt(obj)
                isAssignableTo(NBTDouble.clazz) -> NBTDouble(obj)
                isAssignableTo(NBTByte.clazz) -> NBTByte(obj)
                isAssignableTo(NBTFloat.clazz) -> NBTFloat(obj)
                isAssignableTo(NBTLong.clazz) -> NBTLong(obj)
                isAssignableTo(NBTShort.clazz) -> NBTShort(obj)
                isAssignableTo(NBTIntArray.clazz) -> NBTIntArray(obj)
                isAssignableTo(NBTByteArray.clazz) -> NBTByteArray(obj)
                isAssignableTo(NBTLongArray.clazz) -> NBTLongArray(obj)
                else -> null
            }
        }
    }

    object TiConverter : Converter<TiNBTData> {
        override fun convert(obj: TiNBTData) =
            // 特殊类型直接套 ; 基本类型往下处理
            when (obj) {
                is TiNBTTag -> NBTTag(obj)
                is TiNBTList -> NBTList(obj)
                else -> toNBTData(obj.unsafeData())
            }
    }

    interface Converter<T> {

        fun convert(obj: T): NBTData?

    }

}