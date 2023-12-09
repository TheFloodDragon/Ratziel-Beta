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
fun toNBTData(obj: Any): NBTData = NBTConverter.convert(obj)

@JvmName("toNBTDataNullable")
fun toNBTData(obj: Any?): NBTData? = obj?.let { toNBTData(it) }

/**
 * [net.minecraft.nbt] 中的 NBTBase
 */
val classNBTBase by lazy { nmsClass("NBTBase") }

/**
 * 判断是否为 NmsNBT
 */
fun checkIsNmsNBT(obj: Any?) = obj != null && obj::class.java.isAssignableTo(classNBTBase)