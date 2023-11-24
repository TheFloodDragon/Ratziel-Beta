package cn.fd.ratziel.module.itemengine.nbt

import cn.fd.ratziel.core.util.MirrorClass

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

    companion object : MirrorClass<NBTIntArray>() {

        @JvmStatic
        override val clazz: Class<out Any> by lazy {
            refNBTClass("NBTTagIntArray")
        }

        @JvmStatic
        override fun of(obj: Any) = NBTIntArray(obj)

    }

}