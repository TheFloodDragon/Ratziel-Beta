package cn.fd.ratziel.module.itemengine.nbt

import cn.fd.ratziel.core.function.isAssignableTo
import taboolib.module.nms.NMSItemTag
import taboolib.module.nms.nmsClass
import taboolib.module.nms.nmsProxy

/**
 * 将 TiNBT 转化成 NmsNBT
 */
fun toNmsNBT(tiData: TiNBTData): Any = nmsProxy<NMSItemTag>().itemTagToNMSCopy(tiData)

@JvmName("toNmsNBTKt")
fun TiNBTData.toNmsNBT(): Any = toNmsNBT(this)

/**
 * 将 NmsNBT 转化成 TiNBT
 */
fun toTiNBT(nmsData: Any): TiNBTData = nmsProxy<NMSItemTag>().itemTagToBukkitCopy(nmsData)

/**
 * 将 TiNBT 或者 NmsNBT 转化成 NBTData
 */
fun toNBTData(obj: Any): NBTData =
    (if (obj is NBTData) obj
    else if (obj is TiNBTData) {
        // 特殊类型直接套 ; 基本类型往下处理
        when (obj) {
            is TiNBTTag -> NBTTag(obj)
            is TiNBTList -> NBTList(obj)
            else -> toNBTData(obj.unsafeData())
        }
    } else if (isNmsNBT(obj)) {
        // 麻烦死了
        obj::class.java.run {
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
//                isAssignableTo(NBTLongArray.clazz) -> NBTLongArray(obj) TODO 等更新
                else -> null
            }
        }
    } else when (obj) {
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
//        is LongArray -> NBTLongArray(obj) TODO 等更新
        else -> null
    }) ?: error("Unsupported nbt: $obj (${obj.javaClass})")

@JvmName("toNBTDataKt")
fun toNBTData(obj: Any?): NBTData? = obj?.let { toNBTData(it) }

/**
 * [net.minecraft.nbt] 中的 NBTBase
 */
val classNBTBase by lazy { nmsClass("NBTBase") }

/**
 * 判断是否为 NmsNBT
 */
fun isNmsNBT(obj: Any?) = obj != null && obj::class.java.isAssignableTo(classNBTBase)