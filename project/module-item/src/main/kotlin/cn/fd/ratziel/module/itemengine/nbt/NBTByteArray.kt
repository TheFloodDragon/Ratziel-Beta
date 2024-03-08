package cn.fd.ratziel.module.itemengine.nbt

import taboolib.module.nms.nmsClass

/**
 * NBTByteArray
 *
 * @author TheFloodDragon
 * @since 2023/11/24 22:26
 */
open class NBTByteArray(rawData: Any) : NBTData(
    if (rawData is ByteArray) TiNBTData(rawData) else rawData,
    NBTDataType.BYTE_ARRAY
) {

    companion object {

        @JvmStatic
        val clazz by lazy { nmsClass("NBTTagByteArray") }

        @JvmStatic
        fun of(obj: Any) = NBTByteArray(obj)

    }

}