package cn.fd.ratziel.module.itemengine.nbt

import cn.fd.ratziel.core.util.MirrorClass

/**
 * NBTInt
 *
 * @author TheFloodDragon
 * @since 2023/11/24 22:41
 */
open class NBTInt(rawData: Any) : NBTData(
    if (rawData is Int) TiNBTData(rawData) else rawData,
    NBTDataType.INT
) {

    companion object : MirrorClass<NBTInt>() {

        @JvmStatic
        override val clazz: Class<out Any> by lazy {
            refNBTClass("NBTTagInt")
        }

        @JvmStatic
        override fun of(obj: Any) = NBTInt(obj)

    }

}