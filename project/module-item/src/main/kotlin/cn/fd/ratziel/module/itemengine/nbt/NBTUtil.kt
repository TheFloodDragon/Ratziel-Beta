package cn.fd.ratziel.module.itemengine.nbt

import taboolib.module.nms.MinecraftVersion
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
 * [net.minecraft.nbt] 中的 NBTBase
 */
val nmsNBTBase by lazy {
    refNBTClass("NBTBase")
}

/**
 * 判断是否为 NmsNBT
 */
fun isNmsNBT(obj: Any?) = obj != null && obj::class.java.isAssignableFrom(nmsNBTBase)

/**
 * 内部快捷方法 - 获取 nms 的 NBT相关类
 * @param name NBT类名 (不包括包名)
 */
internal fun refNBTClass(name: String) =
    if (MinecraftVersion.isHigherOrEqual(MinecraftVersion.V1_17))
        Class.forName("net.minecraft.nbt.$name")
    else nmsClass(name)