package cn.fd.ratziel.module.itemengine.nbt

import cn.fd.ratziel.core.function.MirrorClass
import cn.fd.ratziel.core.function.getFieldUnsafe
import taboolib.library.reflex.ReflexClass
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.nmsClass

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

    /**
     * 字符串值
     */
    val content: String
        get() = if (isTiNBT()) getAsTiNBT().asString() else getField.get(data) as String

    companion object : MirrorClass<NBTString>() {

        @JvmStatic
        override val clazz: Class<out Any> by lazy { nmsClass("NBTTagString") }

        @JvmStatic
        override fun of(obj: Any) = NBTString(obj)

        internal val getField by lazy {
            ReflexClass.of(clazz).structure.getFieldUnsafe(
                name = if (MinecraftVersion.isUniversal) "A" else "data",
                String::class.java
            )
        }

    }

}