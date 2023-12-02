package cn.fd.ratziel.module.itemengine.nbt

import cn.fd.ratziel.core.function.MirrorClass
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

    companion object : MirrorClass<NBTLongArray>() {

        @JvmStatic
        override val clazz: Class<out Any> by lazy { nmsClass("NBTTagLongArray") }

        @JvmStatic
        override fun of(obj: Any) = NBTLongArray(obj)

    }

}