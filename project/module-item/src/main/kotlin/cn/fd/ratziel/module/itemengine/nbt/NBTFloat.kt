package cn.fd.ratziel.module.itemengine.nbt

import taboolib.module.nms.nmsClass

/**
 * NBTFloat
 *
 * @author TheFloodDragon
 * @since 2023/11/24 22:49
 */
open class NBTFloat(rawData: Any) : NBTData(
    if (rawData is Float) TiNBTData(rawData) else rawData,
    NBTDataType.FLOAT
) {

    companion object {

        @JvmStatic
        val clazz: Class<out Any> by lazy { nmsClass("NBTTagFloat") }

        @JvmStatic
        fun of(obj: Any) = NBTFloat(obj)

    }

}