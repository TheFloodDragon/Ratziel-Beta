package cn.fd.ratziel.module.itemengine.nbt

import taboolib.module.nms.nmsClass

/**
 * NBTLongArray
 *
 * @author TheFloodDragon
 * @since 2023/11/24 22:47
 */
open class NBTLongArray(rawData: Any) : NBTData(
    if (rawData is LongArray) TiNBTData(rawData) else rawData,
    NBTDataType.LONG_ARRAY
) {

    companion object {

        @JvmStatic
        val clazz: Class<out Any> by lazy { nmsClass("NBTTagLongArray") }

        @JvmStatic
        fun of(obj: Any) = NBTLongArray(obj)

    }

}