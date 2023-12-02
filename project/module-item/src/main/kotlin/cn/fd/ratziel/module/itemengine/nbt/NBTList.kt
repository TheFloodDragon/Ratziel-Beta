package cn.fd.ratziel.module.itemengine.nbt

import cn.fd.ratziel.core.function.MirrorClass
import taboolib.library.reflex.Reflex.Companion.invokeConstructor
import taboolib.module.nms.nmsClass

/**
 * NBTList - NBT列表
 *
 * @author TheFloodDragon
 * @since 2023/11/24 22:18
 */
@Suppress("UNCHECKED_CAST")
open class NBTList(rawData: Any) : NBTData(
    if (rawData is List<*>) {
        // 查找第一个并判断类型
        rawData.takeUnless { it.isEmpty() }?.first()?.let {
            when {
                isNmsNBT(it) -> new(rawData)
                it is TiNBTData -> TiNBTList(rawData as List<TiNBTData>)
                else -> null
            }
        } ?: new()
    } else rawData,
    NBTDataType.LIST
) {

    constructor() : this(TiNBTList())

    companion object : MirrorClass<NBTList>() {

        @JvmStatic
        override val clazz: Class<out Any> by lazy { nmsClass("NBTTagList") }

        @JvmStatic
        override fun of(obj: Any) = NBTList(obj)

        /**
         * NBTTagList#constructor()
         */
        @JvmStatic
        fun new() = clazz.invokeConstructor()

        /**
         * NBTTagList#constructor(List<NBTBase>,byte)
         */
        @JvmStatic
        fun new(list: ArrayList<Any?>) = clazz.invokeConstructor(list, 0)

        @JvmStatic
        fun new(collection: Collection<Any?>) = ArrayList(collection)

    }

}