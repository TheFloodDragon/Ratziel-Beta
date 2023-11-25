package cn.fd.ratziel.module.itemengine.nbt

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
    (if (obj is TiNBTData) {
        // 特殊类型直接套 ; 基本类型往下处理
        when (obj) {
            is TiNBTTag -> NBTTag(obj)
            is TiNBTList -> NBTList(obj)
            else -> toNBTData(obj.unsafeData())
        }
    } else if (isNmsNBT(obj)) {
        // 麻烦死了
        obj::class.java.let {
            if (it.isAssignableFrom(NBTCompound.clazz)) NBTCompound(obj)
            else if (it.isAssignableFrom(NBTList.clazz)) NBTList(obj)
            else if (it.isAssignableFrom(NBTString.clazz)) NBTString(obj)
            else if (it.isAssignableFrom(NBTInt.clazz)) NBTInt(obj)
            else if (it.isAssignableFrom(NBTDouble.clazz)) NBTDouble(obj)
            else if (it.isAssignableFrom(NBTByte.clazz)) NBTByte(obj)
            else if (it.isAssignableFrom(NBTFloat.clazz)) NBTFloat(obj)
            else if (it.isAssignableFrom(NBTLong.clazz)) NBTLong(obj)
            else if (it.isAssignableFrom(NBTShort.clazz)) NBTShort(obj)
            else if (it.isAssignableFrom(NBTIntArray.clazz)) NBTIntArray(obj)
            else if (it.isAssignableFrom(NBTByteArray.clazz)) NBTByteArray(obj)
//            else if (it.isAssignableFrom(NBTLongArray.clazz)) NBTLongArray(obj) TODO 等更新
            else null
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

/**
 * [net.minecraft.nbt] 中的 NBTBase
 */
val classNBTBase by lazy { nmsClass("NBTBase") }

/**
 * 判断是否为 NmsNBT
 */
fun isNmsNBT(obj: Any?) = obj != null && obj::class.java.isAssignableFrom(classNBTBase)