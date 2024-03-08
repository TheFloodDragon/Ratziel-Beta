package cn.fd.ratziel.module.itemengine.nbt

import taboolib.module.nms.nmsClass

/**
 * NBTIntArray
 *
 * @author TheFloodDragon
 * @since 2023/11/24 22:46
 */
open class NBTIntArray(rawData: Any) : NBTData(
    if (rawData is IntArray) TiNBTData(rawData) else rawData,
    NBTDataType.INT_ARRAY
) {

    companion object {

        @JvmStatic
        val clazz by lazy { nmsClass("NBTTagIntArray") }

        @JvmStatic
        fun of(obj: Any) = NBTIntArray(obj)

    }

}