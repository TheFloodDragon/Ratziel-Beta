package cn.fd.ratziel.module.itemengine.nbt

import taboolib.module.nms.nmsClass

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

    companion object {

        @JvmStatic
        val clazz by lazy { nmsClass("NBTTagDouble") }

        @JvmStatic
        fun of(obj: Any) = NBTDouble(obj)

    }

}