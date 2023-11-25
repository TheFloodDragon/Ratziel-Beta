package cn.fd.ratziel.module.itemengine.nbt

import cn.fd.ratziel.core.function.MirrorClass
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

    companion object : MirrorClass<NBTFloat>() {

        @JvmStatic
        override val clazz: Class<out Any> by lazy { nmsClass("NBTTagFloat") }

        @JvmStatic
        override fun of(obj: Any) = NBTFloat(obj)

    }

}