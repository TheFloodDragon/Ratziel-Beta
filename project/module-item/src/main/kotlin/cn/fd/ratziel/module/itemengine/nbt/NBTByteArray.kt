package cn.fd.ratziel.module.itemengine.nbt

import cn.fd.ratziel.core.util.MirrorClass

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

    companion object : MirrorClass<NBTByteArray>() {

        @JvmStatic
        override val clazz: Class<out Any> by lazy {
            refNBTClass("NBTTagByteArray")
        }

        @JvmStatic
        override fun of(obj: Any) = NBTByteArray(obj)

    }

}