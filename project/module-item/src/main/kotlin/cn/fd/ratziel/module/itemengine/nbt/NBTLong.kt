package cn.fd.ratziel.module.itemengine.nbt

import cn.fd.ratziel.core.util.MirrorClass
import taboolib.module.nms.nmsClass

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
        override val clazz: Class<out Any> by lazy { nmsClass("NBTTagLong") }

        @JvmStatic
        override fun of(obj: Any) = NBTLong(obj)

    }

}