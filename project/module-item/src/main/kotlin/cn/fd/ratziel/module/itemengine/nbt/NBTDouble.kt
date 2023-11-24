package cn.fd.ratziel.module.itemengine.nbt

import cn.fd.ratziel.core.util.MirrorClass

/**
 * NBTDouble
 *
 * @author TheFloodDragon
 * @since 2023/11/24 22:50
 */
open class NBTDouble(rawData: Any) : NBTData(
    if (rawData is Double) TiNBTData(rawData) else rawData,
    NBTDataType.DOUBLE
) {

    companion object : MirrorClass<NBTDouble>() {

        @JvmStatic
        override val clazz: Class<out Any> by lazy {
            refNBTClass("NBTTagDouble")
        }

        @JvmStatic
        override fun of(obj: Any) = NBTDouble(obj)

    }

}