package cn.fd.ratziel.module.itemengine.nbt

import cn.fd.ratziel.core.function.MirrorClass
import taboolib.module.nms.nmsClass

/**
 * NBTByte
 *
 * @author TheFloodDragon
 * @since 2023/11/24 22:25
 */
open class NBTByte(rawData: Any) : NBTData(
    if (rawData is Byte) TiNBTData(rawData) else rawData,
    NBTDataType.BYTE
) {

    companion object : MirrorClass<NBTByte>() {

        @JvmStatic
        override val clazz: Class<out Any> by lazy { nmsClass("NBTTagByte") }

        @JvmStatic
        override fun of(obj: Any) = NBTByte(obj)

    }

}