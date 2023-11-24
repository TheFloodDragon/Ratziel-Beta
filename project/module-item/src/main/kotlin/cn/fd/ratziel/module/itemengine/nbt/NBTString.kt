package cn.fd.ratziel.module.itemengine.nbt

import cn.fd.ratziel.core.util.MirrorClass

/**
 * NBTString
 *
 * @author TheFloodDragon
 * @since 2023/11/24 22:51
 */
open class NBTString(rawData: Any) : NBTData(
    if (rawData is String) TiNBTData(rawData) else rawData,
    NBTDataType.STRING
) {

    companion object : MirrorClass<NBTString>() {

        @JvmStatic
        override val clazz: Class<out Any> by lazy {
            refNBTClass("NBTTagString")
        }

        @JvmStatic
        override fun of(obj: Any) = NBTString(obj)

    }

}