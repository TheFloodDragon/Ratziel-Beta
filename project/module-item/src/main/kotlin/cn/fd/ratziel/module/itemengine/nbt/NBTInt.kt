package cn.fd.ratziel.module.itemengine.nbt

import cn.fd.ratziel.core.function.MirrorClass
import cn.fd.ratziel.core.function.getFieldUnsafe
import taboolib.library.reflex.ReflexClass
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.nmsClass

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

    val content: Int
        get() = if (isTiNBT()) getAsTiNBT().asInt() else getField.get(data) as Int

    companion object : MirrorClass<NBTInt>() {

        @JvmStatic
        override val clazz: Class<out Any> by lazy { nmsClass("NBTTagInt") }

        @JvmStatic
        override fun of(obj: Any) = NBTInt(obj)

        internal val getField by lazy {
            ReflexClass.of(clazz).structure.getFieldUnsafe(
                name = if (MinecraftVersion.isUniversal) "c" else "data",
                Integer::class.java
            )
        }

    }

}