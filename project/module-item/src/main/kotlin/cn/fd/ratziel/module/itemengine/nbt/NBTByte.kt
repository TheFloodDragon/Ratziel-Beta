package cn.fd.ratziel.module.itemengine.nbt

import taboolib.module.nms.nmsClass

/**
 * NBTByte
 *
 * @author TheFloodDragon
 * @since 2023/11/24 22:25
 */
open class NBTByte(rawData: Any) : NBTData(
    if (rawData is Byte) TiNBTData(rawData) else rawData,
    NBTDataType.BYTE
) {

    companion object {

        @JvmStatic
        val clazz by lazy { nmsClass("NBTTagByte") }

        @JvmStatic
        fun of(obj: Any) = NBTByte(obj)

    }

}