package cn.fd.ratziel.module.itemengine.nbt

import cn.fd.ratziel.core.util.MirrorClass

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

    companion object : MirrorClass<NBTLong>() {

        @JvmStatic
        override val clazz: Class<out Any> by lazy {
            refNBTClass("NBTTagLong")
        }

        @JvmStatic
        override fun of(obj: Any) = NBTLong(obj)

    }

}