package cn.fd.ratziel.module.itemengine.nbt

import cn.fd.ratziel.core.util.MirrorClass

/**
 * NBTShort
 *
 * @author TheFloodDragon
 * @since 2023/11/24 22:40
 */
open class NBTShort(rawData: Any) : NBTData(
    if (rawData is Short) TiNBTData(rawData) else rawData,
    NBTDataType.SHORT
) {

    companion object : MirrorClass<NBTShort>() {

        @JvmStatic
        override val clazz: Class<out Any> by lazy {
            refNBTClass("NBTTagShort")
        }

        @JvmStatic
        override fun of(obj: Any) = NBTShort(obj)

    }

}