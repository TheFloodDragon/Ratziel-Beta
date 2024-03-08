package cn.fd.ratziel.module.itemengine.nbt

import taboolib.module.nms.nmsClass

/**
 * NBTLong
 *
 * @author TheFloodDragon
 * @since 2023/11/24 22:47
 */
open class NBTLong(rawData: Any) : NBTData(
    if (rawData is Long) TiNBTData(rawData) else rawData,
    NBTDataType.LONG
) {

    companion object {

        @JvmStatic
        val clazz by lazy { nmsClass("NBTTagLong") }

        @JvmStatic
        fun of(obj: Any) = NBTLong(obj)

    }

}