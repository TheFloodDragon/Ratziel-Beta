package cn.fd.ratziel.module.itemengine.api.attribute

import cn.fd.ratziel.module.itemengine.nbt.NBTData
import cn.fd.ratziel.module.itemengine.nbt.NBTTag
import cn.fd.ratziel.module.itemengine.nbt.toNBTData

/**
 * NBTAttribute - NBT属性
 *
 * @author TheFloodDragon
 * @since 2023/12/2 23:26
 */
open class NBTAttribute(
    override val node: String,
    override val value: NBTData? = null,
) : Attribute<NBTData?> {

    constructor(node: String, value: Array<Pair<String, Any?>>) : this(node, getTag(value) { toNBTData(it) })

    companion object {
        @JvmStatic
        fun of(node: String, value: Array<Pair<String, Any?>>) = NBTAttribute(node, value)

        internal fun <T> getTag(array: Array<Pair<String, T>>, tag: NBTTag = NBTTag(), function: (T?) -> NBTData?) =
            tag.apply { array.forEach { handlePair(this, it, function) } }

        internal fun <T> handlePair(tag: NBTTag, pair: Pair<String, T>, function: (T?) -> NBTData?) =
            function.invoke(pair.second)?.let { tag.putDeep(pair.first, it) }
    }

}